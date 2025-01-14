package com.zliang.pg.common.domain;

import com.zliang.pg.common.enums.ErrorKind;

public class Error {
    private final ErrorKind kind;
    private final String message;

    public Error(ErrorKind kind, String message) {
        this.kind = kind;
        this.message = message;
    }

    public ErrorKind getKind() {
        return kind;
    }

    public String getMessage() {
        return message;
    }
}
