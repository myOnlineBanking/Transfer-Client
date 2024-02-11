package com.transfer.TransferClient.controller;

import com.transfer.TransferClient.dto.ErrorClass;
import com.transfer.TransferClient.exceptions.TransferException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TransferException.class)
    public ResponseEntity<ErrorClass> handleChequeException(TransferException ex, HttpServletRequest request) {
        logger.error("Card Service Error" , ex);
        return ResponseEntity.status(ex.getStatus()).body(new ErrorClass(ex.getStatus(), ex.getMessage(), request.getRequestURI()));
    }

}
