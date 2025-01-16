package com.zliang.pg.protocol.pkg.frontend;

import com.zliang.pg.protocol.pkg.FrontendMessage;
import com.zliang.pg.protocol.util.BufferUtil;
import io.netty.buffer.ByteBuf;

/**
 * @author 赵亮
 * @version 1.0
 * @description: Extended Query. Select n rows from existed Portal
 * @date 2025/1/15 13:29
 */
public class Execute implements FrontendMessage {
    // The name of the portal to execute (an empty string selects the unnamed portal).
    String portal;
    // Maximum number of rows to return, if portal contains a query that returns rows (ignored otherwise). Zero denotes “no limit”.
    int maxRows;
    @Override
    public void deserialize(ByteBuf buffer) {
        this.portal = BufferUtil.read_string(buffer);
        this.maxRows = buffer.readInt();
    }
}
