package com.zliang.pg.common.exceptions;

import com.zliang.pg.common.enums.ErrorKind;
import lombok.Getter;

/**
 * 自定义服务端异常
 */
@Getter
public class PgServerException extends RuntimeException {

    private ErrorKind kind;
    private String message;

    public PgServerException(ErrorKind kind, String message) {
        super(message);
        this.kind = kind;
        this.message = message;
    }

}
