package com.zliang.pg.protocol.pkg;

import io.netty.buffer.ByteBuf;

public interface FrontendMessage {

    /**
     * 将字节流转换为对象，可以认为是 read 操作。
     *
     * @param buffer
     */
    void deserialize(ByteBuf buffer);
}
