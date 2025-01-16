package com.zliang.pg.protocol.pkg.frontend;

import com.zliang.pg.common.enums.ProtocolFormat;
import com.zliang.pg.protocol.pkg.FrontendMessage;
import com.zliang.pg.protocol.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.util.List;

/**
 * @author 赵亮
 * @version 1.0
 * @description: Extended Query. Creating Portal from specific Statement by replacing placeholders by real values
 * @date 2025/1/15 13:28
 */
@Getter
public class Bind implements FrontendMessage {
    // The name of the destination portal (an empty string selects the unnamed portal).
    String portal;
    // The name of the source prepared statement (an empty string selects the unnamed prepared statement).
    String statement;
    // Format for parameters
    List<ProtocolFormat> parameterFormats;
    // Raw values for parameters
    List<Byte> parameterValues;
    // Format for results
    List<ProtocolFormat> resultFormats;

    @Override
    public void deserialize(ByteBuf buffer) {
        this.portal = BufferUtil.read_string(buffer);
        this.statement = BufferUtil.read_string(buffer);

        short total = buffer.readShort();
        for (int i = 0; i < total; i++) {
            this.parameterFormats.add(BufferUtil.read_format(buffer));
        }

        total = buffer.readShort();
        for (int i = 0; i < total; i++) {
            int len = buffer.readInt();
            if (len == -1) {
                // None 没有数据
                this.parameterValues.add(null);
            } else {
                this.parameterValues.add(buffer.readByte());
            }
        }

        total = buffer.readShort();
        for (int i = 0; i < total; i++) {
            this.resultFormats.add(BufferUtil.read_format(buffer));
        }
    }
}
