package com.transfer.TransferClient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferDto {
    private String id;

    private double amount;

    private Date date = new Date();

    private String accountFrom;

    private String accountTo;

    private String transferType;

    private String costType;

    private String holdingDate;
}
