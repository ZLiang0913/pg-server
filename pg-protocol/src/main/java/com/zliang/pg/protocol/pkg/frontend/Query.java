package com.zliang.pg.protocol.pkg.frontend;

import com.zliang.pg.protocol.pkg.FrontendMessage;
import com.zliang.pg.protocol.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * @author 赵亮
 * @version 1.0
 * @description: Simple Query
 * @date 2025/1/14 18:23
 */
@Getter
public class Query implements FrontendMessage {
    String query;

    @Override
    public void deserialize(ByteBuf buffer) {
        this.query = BufferUtil.read_string(buffer);
    }
}
