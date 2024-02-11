package com.transfer.TransferClient.mapper;

import com.transfer.TransferClient.dto.TransferDto;
import com.transfer.TransferClient.entity.Transfer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransferMapper {

    public TransferDto map(Transfer transfer) {

        TransferDto transferDto = new TransferDto();

        transferDto.setId(transfer.getId());
        transferDto.setTransferType(transfer.getTransferType());
        transferDto.setAccountFrom(transfer.getAccountFrom());
        transferDto.setAccountTo(transfer.getAccountTo());
        transferDto.setHoldingDate(transfer.getHoldingDate());
        transferDto.setAmount(transfer.getAmount());
        transferDto.setCostType(transfer.getCostType());
        transferDto.setDate(transfer.getDate());
        return transferDto;
    }

    public Transfer map(TransferDto transferDto) {

        Transfer transfer = new Transfer();


        transfer.setId(transferDto.getId());
        transfer.setTransferType(transferDto.getTransferType());
        transfer.setAccountFrom(transferDto.getAccountFrom());
        transfer.setAccountTo(transferDto.getAccountTo());
        transfer.setHoldingDate(transferDto.getHoldingDate());
        transfer.setAmount(transferDto.getAmount());
        transfer.setCostType(transferDto.getCostType());
        transfer.setDate(transferDto.getDate());


        return transfer;
    }
    public List<TransferDto> map(List<Transfer> transfers) {

        return  transfers
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    public List<Transfer> toMap(List<TransferDto> transferDtos) {

        return  transferDtos
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }
}