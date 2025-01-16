package com.zliang.pg.protocol.pkg;

import io.netty.buffer.ByteBuf;


public interface BackendMessage {

    /**
     * 消息类型
     * @return
     */
    char code();

    /**
     * 将对象转换为字节流，可以认为是 write 操作。
     * @param buffer
     */
    ByteBuf serialize(ByteBuf buffer);
}
