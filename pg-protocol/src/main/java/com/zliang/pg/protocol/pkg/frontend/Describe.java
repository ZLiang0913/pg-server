package com.zliang.pg.protocol.pkg.frontend;

import com.zliang.pg.common.enums.ErrorCode;
import com.zliang.pg.common.exceptions.PgErrorException;
import com.zliang.pg.protocol.enums.DescribeType;
import com.zliang.pg.protocol.pkg.FrontendMessage;
import com.zliang.pg.protocol.util.BufferUtil;
import io.netty.buffer.ByteBuf;

/**
 * @author 赵亮
 * @version 1.0
 * @description: Extended Query. Describe Portal/Statement
 * @date 2025/1/15 13:28
 */
public class Describe implements FrontendMessage {
    DescribeType typ;
    String name;

    @Override
    public void deserialize(ByteBuf buffer) {
        byte code = buffer.readByte();
        if (code == 'S') {
            this.typ = DescribeType.Statement;
        } else if (code == 'P') {
            this.typ = DescribeType.Portal;
        } else {
            PgErrorException.error(ErrorCode.ProtocolViolation, "Unknown describe code: " + code);
        }
        this.name = BufferUtil.read_string(buffer);
    }
}
