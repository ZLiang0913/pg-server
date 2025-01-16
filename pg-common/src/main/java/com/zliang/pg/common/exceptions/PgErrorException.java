package com.zliang.pg.common.exceptions;

import com.zliang.pg.common.enums.ErrorCode;
import com.zliang.pg.common.enums.ErrorSeverity;
import lombok.Getter;

/**
 * pg错误响应
 */
@Getter
public class PgErrorException extends RuntimeException {

    private ErrorSeverity severity;
    private ErrorCode code;
    private String message;

    public PgErrorException(ErrorSeverity severity, ErrorCode code, String message) {
        super(message);
        this.severity = severity;
        this.code = code;
        this.message = message;
    }

    public static void error(ErrorCode code, String message) {
        throw new PgErrorException(ErrorSeverity.Error, code, message);
    }

    public static void fatal(ErrorCode code, String message) {
        throw new PgErrorException(ErrorSeverity.Fatal, code, message);
    }

    public static void queryCanceled() {
        throw new PgErrorException(ErrorSeverity.Error, ErrorCode.QueryCanceled, "canceling statement due to user request");
    }

    public static void adminShutdown() {
        throw new PgErrorException(ErrorSeverity.Fatal, ErrorCode.AdminShutdown, "terminating connection due to shutdown signal");
    }

}
