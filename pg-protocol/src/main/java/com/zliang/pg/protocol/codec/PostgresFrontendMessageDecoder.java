package com.zliang.pg.protocol.codec;

import com.zliang.pg.common.util.ByteBufUtils;
import com.zliang.pg.common.vo.Portal;
import com.zliang.pg.protocol.domain.FrontendMessage;
import com.zliang.pg.protocol.domain.FrontendMessageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgresFrontendMessageDecoder extends MessageToMessageDecoder<ByteBuf> {

    private static final Logger logger = LoggerFactory.getLogger(PostgresFrontendMessageDecoder.class);

    private boolean startupMessageSeen = false;
    private boolean sslNegotiationMessageSeen = false;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> list) {
        logger.info("current thread id:{}", Thread.currentThread().getId());
        if (!startupMessageSeen) {
            val length = msg.readInt();
            val protocolVersion = msg.readInt();
            if (protocolVersion == 80877103) {
                // This is an SSL Negotiation message
                sslNegotiationMessageSeen = true;
                logger.debug("SSL Negotiation message seen");
                ctx.fireChannelRead(new FrontendMessage.SSLRequest());
            } else {
                // This is the actual startup message
                startupMessageSeen = true;
                val properties = readStartupMessage(msg);
                val startupMessage = new FrontendMessage.Startup(protocolVersion, properties);
                logger.debug("Startup message seen: {}", startupMessage);
                ctx.fireChannelRead(startupMessage);
            }
        } else {
            // This is a regular message
            Portal currentPortal = new Portal();
            while (true) {
                if (!msg.isReadable()) return;

                char messageId = (char) msg.readByte();
                int messageLength = msg.readInt() - 4;
                logger.debug("Message id: {}, length:{}", messageId, messageLength);
                FrontendMessageType type = FrontendMessageType.fromId(messageId);
                switch (type) {
                    case Query:
                        String query = ByteBufUtils.readString(msg, messageLength);
                        logger.debug("Query: {}", query);
                        if (query != null) {
                            ctx.fireChannelRead(new FrontendMessage.Query(query));
                        }
                        break;
                    case Parse:
                        String parse = ByteBufUtils.readString(msg, messageLength);
                        logger.debug("Parse: {}", parse);
                        if (parse != null) {
                            ctx.fireChannelRead(new FrontendMessage.Parse(parse, currentPortal));
                        }
                        break;
                    case Bind: // Bind
                        String bind = ByteBufUtils.readString(msg, messageLength);
                        ctx.fireChannelRead(new FrontendMessage.Bind(bind));
                        break;
                    case Describe: // Describe
                        String describe = ByteBufUtils.readString(msg, messageLength);
                        ctx.fireChannelRead(new FrontendMessage.Describe(describe, currentPortal));
                        break;
                    case Execute: // Execute
                        String name = ByteBufUtils.readString(msg, messageLength);
                        ctx.fireChannelRead(new FrontendMessage.Execute(name, currentPortal));
                        break;
                    case Sync: // Sync
                        ctx.fireChannelRead(new FrontendMessage.Sync());
                        currentPortal = new Portal();  // Reset portal state
                        break;
                    case Close: {  // Close
                        ctx.fireChannelRead(new FrontendMessage.Close());
                        break;
                    }
                    case Terminate: {  // Terminate
                        logger.info("Client requested termination");
                        break;
                    }
                    default:
                        logger.error("Unknown message type: {}", type);
                        throw new UnsupportedOperationException("Unknown message type: " + type);
                }
            }
        }

    }

    private Map<String, String> readStartupMessage(ByteBuf buffer) {
        Map<String, String> properties = new HashMap<>();
        while (true) {
            String key = ByteBufUtils.readCString(buffer);
            if (key == null) {
                break;
            }
            String value = ByteBufUtils.readCString(buffer);
            logger.trace("payload: key={} value={}", key, value);
            if (!"".equals(key) && !"".equals(value)) {
                properties.put(key, value);
            }
        }
        return properties;
    }

}