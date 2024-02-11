package com.transfer.TransferClient.service;

import com.transfer.TransferClient.dto.ErrorClass;
import com.transfer.TransferClient.dto.TransferDto;
import com.transfer.TransferClient.entity.Transfer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface TransferClientService {

    //account to account
    Transfer transferAccountToAccount(Transfer transfer);

    Transfer transferAccountToCash(Transfer transfer);

    Transfer transferCashToAccount(Transfer transfer);

    Transfer transferCashToCash(Transfer transfer);


    List<Transfer> multipleTransfer( List<Transfer> transfers);

    List<TransferDto> getTransferByAccount(String AccountId,final HttpServletRequest request);

    List<TransferDto> getTransferByClient(String ClienttId,final HttpServletRequest request);

    Transfer getTransferById(String transferId);

    List<Transfer> getAllTransfers();

    ErrorClass withdraw(Map data);
}
