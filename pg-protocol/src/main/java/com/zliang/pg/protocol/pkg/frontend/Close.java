package com.zliang.pg.protocol.pkg.frontend;

import com.zliang.pg.common.enums.ErrorCode;
import com.zliang.pg.common.exceptions.PgErrorException;
import com.zliang.pg.protocol.enums.CloseType;
import com.zliang.pg.protocol.pkg.FrontendMessage;
import com.zliang.pg.protocol.util.BufferUtil;
import io.netty.buffer.ByteBuf;

/**
 * @author 赵亮
 * @version 1.0
 * @description: Extended Query. Close Portal/Statement
 * @date 2025/1/15 13:29
 */
public class Close implements FrontendMessage {
    CloseType typ;
    // The name of the prepared statement or portal to close (an empty string selects the unnamed prepared statement or portal).
    String name;

    @Override
    public void deserialize(ByteBuf buffer) {
        byte code = buffer.readByte();
        if (code == 'S') {
            this.typ = CloseType.Statement;
        } else if (code == 'P') {
            this.typ = CloseType.Portal;
        } else {
            PgErrorException.error(ErrorCode.ProtocolViolation, "Unknown describe code: " + code);
        }
        this.name = BufferUtil.read_string(buffer);
    }
}
