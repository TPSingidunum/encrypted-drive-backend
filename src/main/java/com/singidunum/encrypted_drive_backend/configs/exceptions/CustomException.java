package com.singidunum.encrypted_drive_backend.configs.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;
    private final int errorCode;

    public CustomException(String message, HttpStatus httpStatus, ErrorCode errorCode) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
        this.errorCode = errorCode.getCode();
    }
}
