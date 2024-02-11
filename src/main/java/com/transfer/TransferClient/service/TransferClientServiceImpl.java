package com.transfer.TransferClient.service;

import com.transfer.TransferClient.dto.ErrorClass;
import com.transfer.TransferClient.dto.TransferDto;
import com.transfer.TransferClient.entity.*;
import com.transfer.TransferClient.exceptions.TransferException;
import com.transfer.TransferClient.mapper.TransferMapper;
import com.transfer.TransferClient.repository.TransferRepository;
import org.apache.http.HttpStatus;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Service
@Transactional
public class TransferClientServiceImpl implements TransferClientService {

    Logger logger = Logger.getLogger("myLogger");

    @Value("${myurl.account}")
    private String ACCOUNT_SERVICE_URL;

    @Value("${myurl.cost}")
    private String PARAMETRAGE_SERVICE_URL;

    @Value("${myurl.history}")
    private String HISTORY_SERVICE_URL;

    @Autowired
    private TransferRepository repository;

    @Autowired
    TransferMapper transferMapper = new TransferMapper();

    @Autowired
    WebClient.Builder webClient;


    @Override
    public Transfer transferAccountToAccount(Transfer transfer) {
        try {

            Account accountFrom = Objects.requireNonNull(webClient.build().get()
                    .uri(ACCOUNT_SERVICE_URL + "getByAccountNumber?accountNumber=" + transfer.getAccountFrom())
                    .retrieve()
                    .bodyToMono(Account.class).block());

            Account accountTo = Objects.requireNonNull(webClient.build().get()
                    .uri(ACCOUNT_SERVICE_URL + "getByAccountNumber?accountNumber=" + transfer.getAccountTo()).retrieve()
                    .bodyToMono(Account.class).block());

            //GET payement cost
            ParametrageApi parametrageApi = new ParametrageApi(transfer.getAmount(), transfer.getTransferType());

            parametrageApi = Objects.requireNonNull(webClient.build().post()
                    .uri(PARAMETRAGE_SERVICE_URL)
                    .body(Mono.just(parametrageApi), ParametrageApi.class)
                    .retrieve()
                    .bodyToMono(ParametrageApi.class).block());

            System.out.println(parametrageApi.toString());


            if (isValidTransfer(parametrageApi.isValidTransfer(), (transfer.getAmount() + parametrageApi.getOperationFees()), accountFrom.getBalance())) {

                switch (transfer.getCostType()) {

                    case EFraisType.FROM_ME:
                        accountFrom.setBalance(accountFrom.getBalance() - transfer.getAmount() - parametrageApi.getOperationFees());
                        accountTo.setBalance(accountTo.getBalance() + transfer.getAmount());
                        break;

                    case EFraisType.FROM_OTHER:
                        accountFrom.setBalance(accountFrom.getBalance() - transfer.getAmount());
                        accountTo.setBalance(accountTo.getBalance() + transfer.getAmount() - parametrageApi.getOperationFees());
                        break;

                    case EFraisType.FROM_BOTH:
                        accountFrom.setBalance(accountFrom.getBalance() - transfer.getAmount() - (0.5 * -parametrageApi.getOperationFees()));
                        accountTo.setBalance(accountTo.getBalance() + transfer.getAmount() - (0.5 * -parametrageApi.getOperationFees()));
                        break;

                }

                webClient.build().put().uri(ACCOUNT_SERVICE_URL + "update")
                        .body(Mono.just(accountFrom), Account.class).retrieve()
                        .bodyToMono(Account.class).block();

                webClient.build().put().uri(ACCOUNT_SERVICE_URL + "update")
                        .body(Mono.just(accountTo), Account.class).retrieve()
                        .bodyToMono(Account.class).block();

                transfer.setHoldingDate(parametrageApi.getHoldingTill());
                transfer = repository.save(transfer);



                /*
                //ADD transfer to history
                HistoryApi historyApi = new HistoryApi(accountFrom.getUserId(), accountFrom.getAccountNumber(), "transfer to :" + accountTo.getAccountNumber() + ", Amount :" + transfer.getAmount());

                Objects.requireNonNull(client.post()
                        .uri(HISTORY_SERVICE_URL)
                        .body(Mono.just(historyApi), HistoryApi.class)
                        .retrieve()
                        .bodyToMono(HistoryApi.class).block());
                */
                logger.log(Logger.Level.INFO, "Votre transfert a été effectué avec succées");


                return transfer;
            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new TransferException(HttpStatus.SC_FORBIDDEN, e.getMessage());
            //  throw new TransferException
        }
        return null;
    }

    public boolean isValidTransfer(boolean isValid, double amount, double balance) {
        boolean validTransfer = false;
        if (isValid) {

            if (amount <= balance) {
                validTransfer = true;
            } else
                logger.log(Logger.Level.INFO, "Votre solde est insuffissant !");
        } else {
            logger.log(Logger.Level.INFO, "Vous avez dépassez le plafond autorisé !");
        }

        return validTransfer;
    }

    @Override
    public Transfer transferAccountToCash(Transfer transfer) {
        try {

            Account accountFrom = Objects.requireNonNull(webClient.build().get()
                    .uri(ACCOUNT_SERVICE_URL + "getByAccountNumber?accountNumber=" + transfer.getAccountFrom())
                    .retrieve()
                    .bodyToMono(Account.class).block());


            //GET payement cost
            ParametrageApi parametrageApi = new ParametrageApi(transfer.getAmount(), transfer.getTransferType());

            parametrageApi = Objects.requireNonNull(webClient.build().post()
                    .uri(PARAMETRAGE_SERVICE_URL)
                    .body(Mono.just(parametrageApi), ParametrageApi.class)
                    .retrieve()
                    .bodyToMono(ParametrageApi.class).block());


            if (isValidTransfer(parametrageApi.isValidTransfer(), (transfer.getAmount() + parametrageApi.getOperationFees()), accountFrom.getBalance())) {

                switch (transfer.getCostType()) {

                    case EFraisType.FROM_ME:
                        accountFrom.setBalance(accountFrom.getBalance() - transfer.getAmount() - parametrageApi.getOperationFees());

                        break;

                    case EFraisType.FROM_OTHER:
                        accountFrom.setBalance(accountFrom.getBalance() - transfer.getAmount());
                        transfer.setAmount(transfer.getAmount() - parametrageApi.getOperationFees());
                        break;

                    case EFraisType.FROM_BOTH:
                        accountFrom.setBalance(accountFrom.getBalance() - transfer.getAmount() - (0.5 * -parametrageApi.getOperationFees()));
                        transfer.setAmount(transfer.getAmount() - (0.5 * -parametrageApi.getOperationFees()));
                        break;

                }
                webClient.build().put().uri(ACCOUNT_SERVICE_URL + "update")
                        .body(Mono.just(accountFrom), Account.class).retrieve()
                        .bodyToMono(Account.class).block();

                transfer.setHoldingDate(parametrageApi.getHoldingTill());

                transfer = repository.save(transfer);


                //ADD transfer to history

                /* HistoryApi historyApi= new HistoryApi(accountFrom.getUserId(),accountFrom.getAccountNumber(),"transfer to :"+transfer.getAccountTo()+", Amount :" +transfer.getAmount());

                /*HistoryApi historyApi= new HistoryApi(accountFrom.getUserId(),accountFrom.getAccountNumber(),"transfer to :"+transfer.getAccountTo()+", Amount :" +transfer.getAmount());


               Objects.requireNonNull(client.post()
                        .uri(HISTORY_SERVICE_URL)
                        .body(Mono.just(historyApi), HistoryApi.class)
                        .retrieve()
                        .bodyToMono(HistoryApi.class).block()) ;*/

                logger.log(Logger.Level.INFO, "Votre transfert a été avec succées");

                return transfer;
            }


        } catch (Exception e) {

            e.printStackTrace();
            throw new TransferException(HttpStatus.SC_FORBIDDEN, e.getMessage());
        }

        return null;
    }

    @Override
    public Transfer transferCashToAccount(Transfer transfer) {

        try {

            Account accountTo = Objects.requireNonNull(webClient.build().get()
                    .uri(ACCOUNT_SERVICE_URL + "getByAccountNumber?accountNumber=" + transfer.getAccountTo()).retrieve()
                    .bodyToMono(Account.class).block());

            //GET payement cost
            ParametrageApi parametrageApi = new ParametrageApi(transfer.getAmount(), transfer.getTransferType());

            parametrageApi = Objects.requireNonNull(webClient.build().post()
                    .uri(PARAMETRAGE_SERVICE_URL)
                    .body(Mono.just(parametrageApi), ParametrageApi.class)
                    .retrieve()
                    .bodyToMono(ParametrageApi.class).block());


            if (parametrageApi.isValidTransfer()) {

                switch (transfer.getCostType()) {

                    case EFraisType.FROM_ME:
                        transfer.setAmount(transfer.getAmount() - parametrageApi.getOperationFees());
                        accountTo.setBalance(accountTo.getBalance() + transfer.getAmount());
                        break;

                    case EFraisType.FROM_OTHER:

                        accountTo.setBalance(accountTo.getBalance() + transfer.getAmount() - parametrageApi.getOperationFees());
                        break;

                    case EFraisType.FROM_BOTH:
                        transfer.setAmount(transfer.getAmount() - (0.5 * -parametrageApi.getOperationFees()));
                        accountTo.setBalance(accountTo.getBalance() + transfer.getAmount() - (0.5 * -parametrageApi.getOperationFees()));
                        break;

                }


                webClient.build().put().uri(ACCOUNT_SERVICE_URL + "update")
                        .body(Mono.just(accountTo), Account.class).retrieve()
                        .bodyToMono(Account.class).block();

                transfer.setHoldingDate(parametrageApi.getHoldingTill());
                transfer = repository.save(transfer);

                /*
                //ADD transfer to history

               /* HistoryApi historyApi= new HistoryApi(transfer.getAccountFrom(),transfer.getAccountFrom(),"transfer to :"+transfer.getAccountTo()+", Amount :" +transfer.getAmount());

                HistoryApi historyApi = new HistoryApi(transfer.getAccountFrom(), transfer.getAccountFrom(), "transfer to :" + transfer.getAccountTo() + ", Amount :" + transfer.getAmount());


                Objects.requireNonNull(client.post()
                        .uri(HISTORY_SERVICE_URL)
                        .body(Mono.just(historyApi), HistoryApi.class)
                        .retrieve()

                        .bodyToMono(HistoryApi.class).block()) ;*/


                logger.log(Logger.Level.INFO, "Votre transfert a été avec succées");

                return transfer;

            } else {
                logger.log(Logger.Level.INFO, "Vous avez dépassez le plafond autorisé !");
            }


        } catch (Exception e) {

            e.printStackTrace();
            throw new TransferException(HttpStatus.SC_FORBIDDEN, e.getMessage());
        }

        return null;
    }

    @Override
    public Transfer transferCashToCash(Transfer transfer) {

        try {

            //GET payement cost
            ParametrageApi parametrageApi = new ParametrageApi(transfer.getAmount(), transfer.getTransferType());

            parametrageApi = Objects.requireNonNull(webClient.build().post()
                    .uri(PARAMETRAGE_SERVICE_URL)
                    .body(Mono.just(parametrageApi), ParametrageApi.class)
                    .retrieve()
                    .bodyToMono(ParametrageApi.class).block());


            if (parametrageApi.isValidTransfer()) {

                transfer.setAmount(transfer.getAmount() - parametrageApi.getOperationFees());
                transfer.setHoldingDate(parametrageApi.getHoldingTill());

                transfer = repository.save(transfer);

                /*
                //ADD transfer to history
                /*HistoryApi historyApi= new HistoryApi(transfer.getAccountFrom(),transfer.getAccountFrom(),"transfer to :"+transfer.getAccountTo()+", Amount :" +transfer.getAmount());

                Objects.requireNonNull(client.post()
                        .uri(HISTORY_SERVICE_URL)
                        .body(Mono.just(historyApi), HistoryApi.class)
                        .retrieve()

                        .bodyToMono(HistoryApi.class).block()) ;*/

                logger.log(Logger.Level.INFO, "Votre transfert a été avec succées");

                return transfer;

            } else {

                logger.log(Logger.Level.INFO, "Vous avez dépassez le plafond autorisé !");
            }


        } catch (Exception e) {

            e.printStackTrace();
            throw new TransferException(HttpStatus.SC_FORBIDDEN, e.getMessage());
        }

        return null;
    }

    @Override
    public List<Transfer> multipleTransfer(List<Transfer> transfers) {
        List<Transfer> transfersList = new ArrayList<>();
        for (Transfer transfer : transfers) {
            transferAccountToAccount(transfer);
            transfersList.add(transfer);

        }
        return transfersList;
    }


    @Override
    public List<TransferDto> getTransferByAccount(String AccountId, HttpServletRequest request) {
        return transferMapper.map(repository.getTransferByAccountFrom(AccountId));

    }

    @Override
    public List<TransferDto> getTransferByClient(String ClienttId, final HttpServletRequest request) {

        Account[] accountsClients = Objects.requireNonNull(webClient.build().get()
                .uri(ACCOUNT_SERVICE_URL + "getUserAccounts?userId=" + ClienttId)
                .retrieve()
                .bodyToMono(Account[].class).block());

        List<TransferDto> transfersAccount;

        List<TransferDto> allClientTransfers = new ArrayList<>();

        for (Account accountsClient : accountsClients) {
            transfersAccount = getTransferByAccount(accountsClient.getAccountNumber(), request);
            allClientTransfers.addAll(transfersAccount);
        }
        return allClientTransfers;
    }


    @Override
    public Transfer getTransferById(String transferId) {

        return repository.findById(transferId).orElse(null);
    }


    @Override
    public List<Transfer> getAllTransfers() {
        return repository.findAll();
    }

    @Override
    public ErrorClass withdraw(Map data) {
        Transfer transfer = repository.findById(data.get("reference").toString()).orElse(null);
        if (transfer != null ) {
            if(transfer.getAccountTo().equals(data.get("cin").toString())) {
                if (!transfer.isWithdrawn()) {
                    if (new Date(transfer.getHoldingDate()).before(new Date())) {
                        transfer.setWithdrawn(true);
                        repository.save(transfer);
                        return new ErrorClass(HttpStatus.SC_OK, "Withdraw Succeeded", "/withdraw");
                    } else return new ErrorClass(HttpStatus.SC_FORBIDDEN, "Money Still in hold", "/withdraw");
                } else return new ErrorClass(HttpStatus.SC_FORBIDDEN, "Money Already Withdrawn", "/withdraw");
            }else return new ErrorClass(HttpStatus.SC_NOT_FOUND, "No Transfers For You", "/withdraw");
        } else return new ErrorClass(HttpStatus.SC_NOT_FOUND, "No Transfers For You", "/withdraw");

    }


}
