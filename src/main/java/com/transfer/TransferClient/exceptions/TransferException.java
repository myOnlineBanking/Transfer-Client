package com.transfer.TransferClient.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferException extends RuntimeException{

    private final int status;

    public TransferException(int status , String message){
        super(message);
        this.status = status;
    }
}