package com.transfer.TransferClient.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Account implements Serializable {
    private String id;
    private double balance;
    private boolean accepted ;
    private String accountNumber ;
    private Date creationDate;
    private String currency;
    private boolean deleted ;
    private boolean enabled ;
    private String type ;
    private String userId ;
}
