package com.zliang.pg.protocol.domain.message;

import com.zliang.pg.common.enums.ErrorCode;
import com.zliang.pg.common.enums.ErrorSeverity;
import com.zliang.pg.common.exceptions.PgErrorException;
import com.zliang.pg.protocol.pkg.backend.StartupMessage;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.zliang.pg.common.constants.Constants.*;


public interface InitialMessage {
    static InitialMessage from(ByteBuf buffer) {
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
                    throw new PgErrorException(ErrorSeverity.Error, ErrorCode.ProtocolViolation,
                            "Unsupported special version in initial message with code " + minor);
            }
        } else {
//            buffer.resetReaderIndex();
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

