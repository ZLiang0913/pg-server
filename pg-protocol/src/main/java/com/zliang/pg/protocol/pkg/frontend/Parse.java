package com.zliang.pg.protocol.pkg.frontend;

import com.zliang.pg.protocol.pkg.FrontendMessage;
import com.zliang.pg.protocol.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 赵亮
 * @version 1.0
 * @description: Extended Query. Create Statement.
 * @date 2025/1/14 18:23
 */
@Getter
public class Parse implements FrontendMessage {
    // The name of the prepared statement. Empty string is used for unamed statements
    String name;
    // SQL query with placeholders ($1)
    String query;
    // Types for parameters
    List<Integer> paramTypes;

    @Override
    public void deserialize(ByteBuf buffer) {
        this.name = BufferUtil.read_string(buffer);
        this.query = BufferUtil.read_string(buffer);
        short total = buffer.readShort();
        paramTypes = new ArrayList<>(total);
        for (int i = 0; i < total; i++) {
            paramTypes.add(buffer.readInt());
        }
    }
}
