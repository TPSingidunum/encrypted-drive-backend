package com.singidunum.encrypted_drive_backend.configs.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    private int status;
    private int errorCode;
    private Object message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ErrorResponse(int status, int errorCode, Object message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }
}
