package com.zliang.pg.common.enums;

public enum ErrorSeverity {
    // https://www.postgresql.org/docs/14/protocol-error-fields.html
    Error,
    Fatal,
    Panic,
}