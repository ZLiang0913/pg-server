package com.zliang.pg.protocol.util;

import com.zliang.pg.common.enums.ErrorCode;
import com.zliang.pg.common.exceptions.PgErrorException;
import com.zliang.pg.protocol.pkg.FrontendMessage;
import com.zliang.pg.protocol.pkg.frontend.*;
import io.netty.buffer.ByteBuf;

/**
 * @author 赵亮
 * @version 1.0
 * @description: TODO
 * @date 2025/1/14 18:12
 */
public class MessageTagParser {
    public static FrontendMessage parse(byte tag, ByteBuf buffer) {
        FrontendMessage message = null;
        switch (tag) {
            case 'Q':
                message = new Query();
                message.deserialize(buffer);
                break;
            case 'P':
                message = new Parse();
                message.deserialize(buffer);
                break;
            case 'B':
                message = new Bind();
                message.deserialize(buffer);
                break;
            case 'D':
                message = new Describe();
                message.deserialize(buffer);
                break;
            case 'E':
                message = new Execute();
                message.deserialize(buffer);
                break;
            case 'C':
                message = new Close();
                message.deserialize(buffer);
                break;
            case 'p':
                message = new PasswordMessage();
                message.deserialize(buffer);
                break;
            case 'X':
                message = new Terminate();
                break;
            case 'H':
                message = new Flush();
                break;
            case 'S':
                message = new Sync();
                break;
            default:
                PgErrorException.error(ErrorCode.DataException, "Unknown message type: " + tag);
        }
        return message;
    }
}
