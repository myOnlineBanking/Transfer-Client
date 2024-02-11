package com.transfer.TransferClient.entity;

import lombok.*;

import java.io.Serializable;
@Data
@NoArgsConstructor
public class ParametrageApi implements Serializable {

    private String id;
    private double transferAmount;
    private String transferType ;
    private double operationFees ;
    private boolean validTransfer ;
    private String holdingTill;

    public ParametrageApi(double amount, String type) {
        this.transferAmount=amount;
        this.transferType=type;
    }

}
