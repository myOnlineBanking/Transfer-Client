package com.transfer.TransferClient.entity;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "transfers")
@Data
public class Transfer {

    @Id
    private String id;

    @NotBlank
    private double amount;

    private Date date = new Date();


    private String accountFrom;


    private String accountTo;

    @NotBlank
    private String transferType;

    @NotBlank
    private String costType;


    @NotBlank
    private String holdingDate;

    private boolean withdrawn = false;

}
