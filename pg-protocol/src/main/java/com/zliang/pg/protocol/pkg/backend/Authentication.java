package com.zliang.pg.protocol.pkg.backend;

import com.zliang.pg.protocol.enums.AuthenticationRequest;
import com.zliang.pg.protocol.enums.BackendMessageType;
import com.zliang.pg.protocol.pkg.BackendMessage;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 赵亮
 * @version 1.0
 * @description: TODO
 * @date 2025/1/16 15:23
 */
@Data
@AllArgsConstructor
public class Authentication implements BackendMessage {
    AuthenticationRequest response;

    @Override
    public char code() {
        return BackendMessageType.Authentication.getId();
    }

    @Override
    public ByteBuf serialize(ByteBuf buffer) {
        buffer.writeBytes(this.response.name().getBytes());
        return buffer;
    }
}
