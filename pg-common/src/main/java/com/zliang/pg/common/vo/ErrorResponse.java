package com.zliang.pg.common.vo;

import com.zliang.pg.common.enums.ErrorCode;
import com.zliang.pg.common.enums.ErrorSeverity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author 赵亮
 * @version 1.0
 * @description: TODO
 * @date 2025/1/7 15:49
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    // https://www.postgresql.org/docs/14/protocol-error-fields.html
    ErrorSeverity severity;
    ErrorCode code;
    String message;
}
