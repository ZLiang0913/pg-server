package com.zliang.pg.protocol.pkg.backend;

import com.zliang.pg.common.enums.ErrorCode;
import com.zliang.pg.common.enums.NoticeSeverity;
import com.zliang.pg.protocol.enums.BackendMessageType;
import com.zliang.pg.protocol.pkg.BackendMessage;
import com.zliang.pg.protocol.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * @author 赵亮
 * @version 1.0
 * @description: 通知消息
 * @date 2025/1/14 16:06
 */
@Data
@ToString(callSuper = true)
@AllArgsConstructor()
public class NoticeResponse implements BackendMessage {
    // https://www.postgresql.org/docs/14/protocol-error-fields.html
    NoticeSeverity severity;
    ErrorCode code;
    String message;

    public NoticeResponse warning(ErrorCode code, String message) {
        return new NoticeResponse(NoticeSeverity.Warning, code, message);
    }


    @Override
    public char code() {
        return BackendMessageType.NoticeResponse.getId();
    }

    @Override
    public ByteBuf serialize(ByteBuf buffer) {
        var severity = this.severity.name();
        buffer.writeByte('S');
        BufferUtil.write_string(buffer, severity);
        buffer.writeByte('C');
        BufferUtil.write_string(buffer, this.code.name());
        buffer.writeByte('M');
        BufferUtil.write_string(buffer, this.message);
        buffer.writeByte(0);

        return buffer;
    }
}
