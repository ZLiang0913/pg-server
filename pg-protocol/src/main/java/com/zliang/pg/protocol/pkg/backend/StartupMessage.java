package com.zliang.pg.protocol.pkg.backend;

import com.zliang.pg.protocol.pkg.BackendMessage;
import com.zliang.pg.protocol.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class StartupMessage implements BackendMessage {
    short major;
    short minor;
    HashMap<String, String> parameters;

    public static StartupMessage from(ByteBuf buffer) {
//            Startup packet
//            int32 len int32 protocol str name \0 str value ... \0
        short major = buffer.readShort();
        short minor = buffer.readShort();
        HashMap parameters = new HashMap();
        while (true) {
            String name = BufferUtil.read_string(buffer);
            if (name == null) {
                break;
            }
            String value = BufferUtil.read_string(buffer);
            parameters.put(name, value);
        }
        return new StartupMessage(major, minor, parameters);
    }

    @Override
    public char code() {
        return '0';
    }

    @Override
    public ByteBuf serialize(ByteBuf buffer) {
        buffer.writeShort(this.major);
        buffer.writeShort(this.minor);
        this.parameters.forEach((name, value) -> {
            BufferUtil.write_string(buffer, name);
            BufferUtil.write_string(buffer, value);
        });
        buffer.writeInt(0);
        return buffer;
    }
}