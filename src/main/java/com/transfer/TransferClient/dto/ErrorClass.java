package com.transfer.TransferClient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorClass {

    private int status;
    private String error;
    private String path;

}
