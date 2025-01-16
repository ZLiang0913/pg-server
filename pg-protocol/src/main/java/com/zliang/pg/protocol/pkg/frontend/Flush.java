package com.zliang.pg.protocol.pkg.frontend;

import com.zliang.pg.protocol.pkg.FrontendMessage;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * @author 赵亮
 * @version 1.0
 * @description: Flush network buffer
 * @date 2025/1/14 18:23
 */
@Getter
public class Flush implements FrontendMessage {

    @Override
    public void deserialize(ByteBuf buffer) {

    }
}
