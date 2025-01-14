package com.zliang.pg.common.enums;

public enum ErrorCode {
    // 0A — Feature Not Supported
    FeatureNotSupported,
    // 8 -  Connection Exception
    ProtocolViolation,
    // 28 - Invalid Authorization Specification
    InvalidAuthorizationSpecification,
    InvalidPassword,
    // 22
    DataException,
    // Class 25 — Invalid Transaction State
    ActiveSqlTransaction,
    NoActiveSqlTransaction,
    // 26
    InvalidSqlStatement,
    // 34
    InvalidCursorName,
    // Class 42 — Syntax Error or Access Rule Violation
    DuplicateCursor,
    SyntaxError,
    // Class 53 — Insufficient Resources
    TooManyConnections,
    ConfigurationLimitExceeded,
    // Class 55 — Object Not In Prerequisite State
    ObjectNotInPrerequisiteState,
    // Class 57 - Operator Intervention
    QueryCanceled,
    AdminShutdown,
    // XX - Internal Error
    InternalError,
}