package com.zliang.pg.protocol.pkg.frontend;

import com.zliang.pg.protocol.pkg.FrontendMessage;
import io.netty.buffer.ByteBuf;

/**
 * @author 赵亮
 * @version 1.0
 * @description: Sync primitive in Extended Query for error recovery.
 * @date 2025/1/15 13:27
 */
public class Sync implements FrontendMessage {
    @Override
    public void deserialize(ByteBuf buffer) {

    }
}
