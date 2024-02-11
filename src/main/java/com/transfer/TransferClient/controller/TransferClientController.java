package com.transfer.TransferClient.controller;


import com.transfer.TransferClient.dto.ErrorClass;
import com.transfer.TransferClient.dto.TransferDto;
import com.transfer.TransferClient.entity.Transfer;
import com.transfer.TransferClient.service.TransferClientService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/transfer-client")
public class TransferClientController {


    Logger logger = Logger.getLogger("myLogger");

    @Autowired
    private TransferClientService transferClientService;


    @PostMapping("/transferAccountToAccount")
    public Transfer transferAccountToAccount(@RequestBody Transfer transfer) {

        logger.log(Logger.Level.INFO, "add transferAccountToAccount: " + transfer.toString());
        return transferClientService.transferAccountToAccount(transfer);
    }

    @PostMapping("/transferAccountToCash")
    public Transfer transferAccountToCash(@RequestBody Transfer transfer) {
        logger.log(Logger.Level.INFO, "add transferAccountToCash: " + transfer.toString());
        return transferClientService.transferAccountToCash(transfer);

    }

    @PostMapping("/transferCashToAccount")
    public Transfer transferCashToAccount(@RequestBody Transfer transfer) {
        logger.log(Logger.Level.INFO, "add transferCashToAccount: " + transfer.toString());
        return transferClientService.transferCashToAccount(transfer);

    }

    @PostMapping("/transferCashToCash")
    public Transfer transferCashToCash(@RequestBody Transfer transfer) {

        logger.log(Logger.Level.INFO, "add transferCashToCash: " + transfer.toString());
        return transferClientService.transferCashToCash(transfer);

    }


    @PostMapping("/multipleTransfer")
    public List<Transfer> multipleTransfer(@RequestBody List<Transfer> transfers) {

        logger.log(Logger.Level.INFO, "add multipleTransfer: " + transfers.toString());
        return transferClientService.multipleTransfer(transfers);

    }

    @GetMapping("/getTransfer")
    public Transfer getTransfer(@RequestParam String transferID) {

        logger.log(Logger.Level.INFO, transferID);
        return transferClientService.getTransferById(transferID);
    }

    @GetMapping("/getTransferByAccount")
    public List<TransferDto> getTransferByAccount(@RequestParam String accountFrom, final HttpServletRequest request) {

        logger.log(Logger.Level.INFO, "getTransferByAccount : " + accountFrom);
        return transferClientService.getTransferByAccount(accountFrom, request);
    }

    @GetMapping("/getTransferByClient")
    public List<TransferDto> getTransferByClient(@RequestParam String ClienttId, final HttpServletRequest request) {

        logger.log(Logger.Level.INFO, "getTransferByClient : " + ClienttId);
        return transferClientService.getTransferByClient(ClienttId, request);
    }


    @GetMapping("/transfers")
    public List<Transfer> getAllTransfers() {
        logger.log(Logger.Level.INFO, "all transfers : ");
        return transferClientService.getAllTransfers();
    }

    @PostMapping("/withdraw")
    public ErrorClass withdraw(@RequestBody Map data) {
        logger.log(Logger.Level.INFO, "Withdrawing :" + data);
        return transferClientService.withdraw(data);
    }


}
