package com.zliang.pg.protocol.codec;

import com.zliang.pg.protocol.common.ChannelAttributeKey;
import com.zliang.pg.protocol.common.ConnectionAttr;
import com.zliang.pg.protocol.domain.message.InitialMessage;
import com.zliang.pg.protocol.domain.StartupState;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;

/**
 * @author 赵亮
 * @version 1.0
 * @description: TODO
 * @date 2024/12/23 14:34
 */
@Slf4j
public class PostgreSQLDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        processInitialMessage(channelHandlerContext, byteBuf);

//        if (byteBuf.isReadable(4)) {
//            int packetSize = byteBuf.getUnsignedMediumLE(0);
//            if (byteBuf.isReadable(packetSize + 3/*Packet Length*/ + 1/*Packet Num*/)) {
//
//            } else {
//                // data is not full
//                log.warn("wait data {}", byteBuf);
//                return;
//            }
//        } else {
//            log.warn("no data {}", byteBuf);
//            return;
//        }
//        list.add(decode(byteBuf));
        /*if (!startupMessageSeen) {
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
                        if (query!= null) {
                            ctx.fireChannelRead(new FrontendMessage.Query(query));
                        }
                        break;
                    case Parse:
                        String parse = ByteBufUtils.readString(msg, messageLength);
                        logger.debug("Parse: {}", parse);
                        if (parse!= null) {
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
                    case PortalSuspended: {
                        logger.info("PortalSuspended");
                        break;
                    }
                    default:
                        logger.error("Unknown message type: {}", type);
                        throw new UnsupportedOperationException("Unknown message type: " + type);
                }
            }
        }*/
    }

    StartupState processInitialMessage(ChannelHandlerContext ctx, ByteBuf bufferBuf) {
//        let mut buffer = buffer::read_contents(&mut self.socket, 0).await?;

        InitialMessage initialMessage = InitialMessage.from(bufferBuf);
        if (initialMessage instanceof InitialMessage.Startup startup) {
            return processStartupMessage(ctx, startup.startup());
        } else if (initialMessage instanceof InitialMessage.Cancel cancel) {
            return processCancel(ctx, cancel.cancel());
        } else {//initialMessage instanceof InitialMessage.Gssenc || initialMessage instanceof InitialMessage.Ssl
            ByteBuf buffer = ctx.alloc().buffer();
            buffer.writeByte('N');
            ctx.writeAndFlush(buffer);
            return new StartupState.SslRequested();
        }
    }

    StartupState processStartupMessage(ChannelHandlerContext ctx, InitialMessage.StartupMessage startupMessage) {
        if(startupMessage.getMajor() != 3 || startupMessage.getMinor() != 0) {
            /*let error_response = protocol::ErrorResponse::new(
                    protocol::ErrorSeverity::Fatal,
                    protocol::ErrorCode::FeatureNotSupported,
                    format!(
                    "unsupported frontend protocol {}.{}: server supports 3.0 to 3.0",
                    startup_message.major, startup_message.minor,
                ),
            );
            buffer::write_message(&mut self.socket, error_response).await?;*/
            return new StartupState.Denied();
        }

        HashMap<String, String> parameters = startupMessage.getParameters();
        if (!parameters.containsKey("user")) {
            /*let error_response = protocol::ErrorResponse::new(
                    protocol::ErrorSeverity::Fatal,
                    protocol::ErrorCode::InvalidAuthorizationSpecification,
                    "no PostgreSQL user name specified in startup packet".to_string(),
            );
            buffer::write_message(&mut self.socket, error_response).await?;*/
            return new StartupState.Denied();
        }

        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeByte('R');
        ctx.writeAndFlush(buffer);
        return new StartupState.Success(parameters);
    }

    StartupState processCancel(ChannelHandlerContext ctx, InitialMessage.CancelRequest canceMessage) {
        log.trace("Cancel request {}", canceMessage);

        ConnectionAttr connectionAttr = ctx.channel().attr(ChannelAttributeKey.CONN_ATTR).get();
        /*if let Some(s) = self
                .session
                .session_manager
                .get_session(cancel_message.process_id)
                .await
        {
            if s.state.secret == cancel_message.secret {
            s.state.cancel_query();
        } else {
            trace!(
                    "Unable to process cancel: wrong secret, {} != {}",
                    s.state.secret,
                    cancel_message.secret
                );
        }
        } else {
            trace!("Unable to process cancel: unknown session");
        }*/
        return new StartupState.CancelRequest();
    }
}
