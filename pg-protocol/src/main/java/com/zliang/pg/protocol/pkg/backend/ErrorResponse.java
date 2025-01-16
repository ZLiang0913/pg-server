package com.zliang.pg.protocol.pkg.backend;

import com.zliang.pg.common.enums.ErrorCode;
import com.zliang.pg.common.enums.ErrorSeverity;
import com.zliang.pg.protocol.enums.BackendMessageType;
import com.zliang.pg.protocol.pkg.BackendMessage;
import com.zliang.pg.protocol.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author 赵亮
 * @version 1.0
 * @description: 错误消息
 * @date 2025/1/14 16:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class ErrorResponse implements BackendMessage {
    // https://www.postgresql.org/docs/14/protocol-error-fields.html
    ErrorSeverity severity;
    ErrorCode code;
    String message;

    public static ErrorResponse error(ErrorCode code, String message) {
        return new ErrorResponse(ErrorSeverity.Error, code, message);
    }

    public static ErrorResponse fatal(ErrorCode code, String message) {
        return new ErrorResponse(ErrorSeverity.Fatal, code, message);
    }

    public static ErrorResponse queryCanceled() {
        return new ErrorResponse(ErrorSeverity.Error, ErrorCode.QueryCanceled,
                "canceling statement due to user request");
    }

    public static ErrorResponse adminShutdown() {
        return new ErrorResponse(ErrorSeverity.Fatal, ErrorCode.AdminShutdown,
                "terminating connection due to shutdown signal");
    }

    @Override
    public char code() {
        return BackendMessageType.ErrorResponse.getId();
    }

    @Override
    public ByteBuf serialize(ByteBuf buffer) {
        var severity = this.severity.name();
        buffer.writeByte('S');
        BufferUtil.write_string(buffer, severity);
        buffer.writeByte('V');
        BufferUtil.write_string(buffer, severity);
        buffer.writeByte('C');
        BufferUtil.write_string(buffer, this.code.name());
        buffer.writeByte('M');
        BufferUtil.write_string(buffer, this.message);
        buffer.writeByte(0);

        return buffer;
    }
}
