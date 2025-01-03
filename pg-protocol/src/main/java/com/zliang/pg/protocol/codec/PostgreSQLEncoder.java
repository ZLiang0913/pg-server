package com.zliang.pg.protocol.codec;

import com.zliang.pg.protocol.domain.BackendMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author 赵亮
 * @version 1.0
 * @description: TODO
 * @date 2024/12/23 14:34
 */
public class PostgreSQLEncoder extends MessageToByteEncoder<BackendMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BackendMessage backendMessage, ByteBuf byteBuf) throws Exception {

    }
}
