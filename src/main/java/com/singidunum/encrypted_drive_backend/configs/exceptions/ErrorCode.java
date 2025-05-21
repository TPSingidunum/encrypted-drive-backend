package com.singidunum.encrypted_drive_backend.configs.exceptions;

import lombok.Getter;

@Getter
public enum ErrorCode {
    BASE_ERROR(-1),
    VALIDATION(-2),
    JWT_CLAIMS_MISSING(-3),
    NOT_VERIFIED(1001),
    USER_NOT_EXIST(1002),
    USER_EMAIL_EXIST(1003),
    USER_USERNAME_EXIST(1004),
    PASSWORDS_DO_NOT_MATCH(1005),
    FAILED_WORKSPACE_CREATION(1006);

    private final int code;
    ErrorCode(int code) {
        this.code = code;
    }
}
