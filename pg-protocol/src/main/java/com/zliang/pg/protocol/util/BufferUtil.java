package com.zliang.pg.protocol.util;

import com.zliang.pg.common.enums.ErrorCode;
import com.zliang.pg.common.enums.ErrorKind;
import com.zliang.pg.common.enums.ProtocolFormat;
import com.zliang.pg.common.exceptions.PgErrorException;
import com.zliang.pg.common.exceptions.PgServerException;
import com.zliang.pg.protocol.pkg.BackendMessage;
import com.zliang.pg.protocol.pkg.FrontendMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static io.netty.util.CharsetUtil.UTF_8;

/**
 * @author 赵亮
 * @version 1.0
 * @description: TODO
 * @date 2025/1/15 9:06
 */
@Slf4j
public class BufferUtil {

    public static FrontendMessage read_message(ByteBuf buf) {
        // https://www.postgresql.org/docs/14/protocol-message-formats.html
        byte message_tag = buf.readByte();
        read_contents(buf, message_tag);
        FrontendMessage message = MessageTagParser.parse(message_tag, buf);
        log.trace("[pg] Decoded {}", message);
        return message;
    }

    public static ByteBuf read_contents(ByteBuf buf, byte messageTag) {
        // protocol defines length for all types of messages
        int length = buf.readInt();
        if (length < 4) {
            throw new PgServerException(ErrorKind.Other,
                    "Unexpectedly small (<0) message size");
        }
        log.trace("[pg] Receive package {} with length {}", messageTag, length);
        length = length - 4;
        if (length < 0) {
            throw new PgServerException(ErrorKind.OutOfMemory,
                    "Unable to convert message length to a suitable memory size");
        }
        return buf;
    }

    public static String read_string(ByteBuf buf) {
        // PostgreSQL uses a null-terminated string (C-style string)
        // throw new PgServerException(ErrorKind.InvalidData, "Unable to parse bytes as a UTF-8 string");
        return new String(readNullTerminatedBytes(buf), UTF_8);
    }

    public static ProtocolFormat read_format(ByteBuf buf) {
        ProtocolFormat format = null;
        short code = buf.readShort();
        if (code == 0) {
            format = ProtocolFormat.Text;
        } else if (code == 1) {
            format = ProtocolFormat.Binary;
        } else {
            PgErrorException.error(ErrorCode.ProtocolViolation, "Unknown format code: " + code);
        }
        return format;
    }

    public static byte[] readNullTerminatedBytes(ByteBuf buf) {
        int length = buf.bytesBefore((byte) 0);
        byte[] bytes = new byte[length + 1];
        buf.readBytes(bytes);
        return bytes;
    }


    //Same as the write_message function, but it doesn’t append header for frame (code + size).
    public static void write_direct(ByteBuf buf, BackendMessage message) {
        message.serialize(buf);
    }

    public static void write_string(ByteBuf buf, String data) {
        buf.writeCharSequence(data, UTF_8);
        buf.writeByte(0x00);
    }
}
