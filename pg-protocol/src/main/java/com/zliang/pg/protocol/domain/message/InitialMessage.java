package com.zliang.pg.protocol.domain.message;

import com.zliang.pg.protocol.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.HashMap;

import static com.zliang.pg.protocol.common.Constants.*;


public interface InitialMessage {
    static InitialMessage from(ByteBuf buffer) {
        int messageLength = buffer.readInt();

        var major = buffer.readShort();
        var minor = buffer.readShort();
        if (major == VERSION_MAJOR_SPECIAL) {
            switch (minor) {
                case VERSION_MINOR_CANCEL:
                    return new Cancel(CancelRequest.from(buffer));
                case VERSION_MINOR_SSL:
                    return new Ssl();
                case VERSION_MINOR_GSSENC:
                    return new Gssenc();
                default:
                   throw new RuntimeException("Unsupported special version in initial message with code "+minor);
            }
        } else {
            buffer.resetReaderIndex();
            return new Startup(StartupMessage.from(buffer));
        }
    }
    record Startup(StartupMessage startup) implements InitialMessage {

    }

    record Cancel(CancelRequest cancel) implements InitialMessage {

    }

    record Ssl() implements InitialMessage {

    }

    record Gssenc() implements InitialMessage {

    }

    @Data
    @AllArgsConstructor
    class StartupMessage {
        short major;
        short minor;
        HashMap<String, String> parameters;

        static StartupMessage from(ByteBuf buffer) {
//            Startup packet
//            int32 len int32 protocol str name \0 str value ... \0
            int len = buffer.readInt();
            short major = buffer.readShort();
            short minor = buffer.readShort();
            HashMap parameters = new HashMap();
            while (true) {
                String name = ByteBufUtils.readCString(buffer);
                if (name == null) {
                    break;
                }
                String value = ByteBufUtils.readCString(buffer);
                parameters.put(name, value);
            }
            return new StartupMessage(major, minor, parameters);
        }
    }

    @Data
    @AllArgsConstructor
    class CancelRequest {
        int processId;
        int secret;

        static CancelRequest from(ByteBuf buffer) {
            int processId = buffer.readInt();
            int secret = buffer.readInt();
            return new CancelRequest(processId, secret);
        }
    }
}

