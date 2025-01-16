package com.zliang.pg.protocol.codec;

import com.zliang.pg.common.enums.ErrorCode;
import com.zliang.pg.common.exceptions.PgErrorException;
import com.zliang.pg.protocol.pkg.BackendMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 赵亮
 * @version 1.0
 * @description: TODO
 * @date 2024/12/23 14:34
 */
@Slf4j
public class PostgreSQLEncoder extends MessageToByteEncoder<BackendMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BackendMessage backendMessage, ByteBuf byteBuf) throws Exception {
        message_serialize(backendMessage, byteBuf);
    }

    private void message_serialize(BackendMessage message, ByteBuf buf) {
        if (message.code() != 0x00) {
            buf.writeByte(message.code());
        }
        // 标记当前的写入位置
        buf.markWriterIndex();
        try {
            ByteBuf buffer = message.serialize(buf);
            int size = buffer.readableBytes() + 4;// 四个字节标识消息内容的长度（该长度包括这四个字节本身）
            // 插入新数据
            buffer.resetWriterIndex();
            buffer.writeByte(size);
        } catch (Exception e) {
            log.error("[pg] Failed to serialize message: {}", message, e);
            PgErrorException.error(ErrorCode.InternalError,
                    "Unable to convert buffer length to a suitable memory size");
        }
    }
}
