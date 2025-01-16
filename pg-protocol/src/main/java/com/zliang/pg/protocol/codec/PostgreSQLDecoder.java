package com.zliang.pg.protocol.codec;

import com.zliang.pg.common.enums.ErrorCode;
import com.zliang.pg.protocol.domain.StartupState;
import com.zliang.pg.protocol.domain.message.InitialMessage;
import com.zliang.pg.protocol.enums.AuthenticationRequest;
import com.zliang.pg.protocol.pkg.backend.ErrorResponse;
import com.zliang.pg.protocol.pkg.backend.StartupMessage;
import com.zliang.pg.protocol.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

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
    private boolean startupMessageSeen = false;
    private boolean sslNegotiationMessageSeen = false;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        HashMap<String, String> initialParameters = null;
        /*StartupState startupState = processInitialMessage(channelHandlerContext, byteBuf);
        if (startupState instanceof StartupState.Success success) {
            initialParameters = success.parameters();
        } else if (startupState instanceof StartupState.SslRequested sslRequested) {
            startupState = processInitialMessage(channelHandlerContext, byteBuf);
            if (startupState instanceof StartupState.Success success) {
                initialParameters = success.parameters();
            } else {
                initialParameters = new HashMap<>();
            }
        } else {
            initialParameters = new HashMap<>();
        }
        System.out.println("initialParameters:" + initialParameters);*/

        if (!startupMessageSeen) {
            BufferUtil.read_contents(byteBuf, (byte) 0);
            InitialMessage initialMessage = InitialMessage.from(byteBuf);
            if (initialMessage instanceof InitialMessage.Startup startup) {
                // This is the actual startup message
                log.debug("Startup message seen: {}", startup);
                startupMessageSeen = true;
                StartupState startupState = processStartupMessage(channelHandlerContext, startup.startup());
                if (startupState instanceof StartupState.Success success) {
                    initialParameters = success.parameters();
                } else {
                    initialParameters = new HashMap<>();
                }
            } else if (initialMessage instanceof InitialMessage.Cancel cancel) {
                processCancel(channelHandlerContext, cancel.cancel());
            } else {//initialMessage instanceof InitialMessage.Gssenc || initialMessage instanceof InitialMessage.Ssl
                log.debug("SSL Negotiation message seen");
                sslNegotiationMessageSeen = true;
                ByteBuf buffer = channelHandlerContext.alloc().buffer();
                buffer.writeByte('N');
                channelHandlerContext.writeAndFlush(buffer);
            }


        } else {

        }
        System.out.println("initialParameters:" + initialParameters);
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
        bufferBuf = BufferUtil.read_contents(bufferBuf, (byte) 0);

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

    StartupState processStartupMessage(ChannelHandlerContext ctx, StartupMessage startupMessage) {
        if (startupMessage.getMajor() != 3 || startupMessage.getMinor() != 0) {
            ErrorResponse errorResponse = ErrorResponse.fatal(ErrorCode.FeatureNotSupported,
                    String.format("unsupported frontend protocol %d.%d: server supports 3.0 to 3.0", startupMessage.getMajor(), startupMessage.getMinor()));
            ctx.writeAndFlush(errorResponse);
            return new StartupState.Denied();
        }

        HashMap<String, String> parameters = startupMessage.getParameters();
        if (!parameters.containsKey("user")) {
            ErrorResponse errorResponse = ErrorResponse.fatal(ErrorCode.InvalidAuthorizationSpecification,
                    "no PostgreSQL user name specified in startup packet");
            ctx.writeAndFlush(errorResponse);
            return new StartupState.Denied();
        }

        AuthenticationRequest authMethod = AuthenticationRequest.CleartextPassword;
        ctx.writeAndFlush(authMethod);
        return new StartupState.Success(parameters, authMethod);
    }

    StartupState processCancel(ChannelHandlerContext ctx, InitialMessage.CancelRequest canceMessage) {
        log.trace("Cancel request {}", canceMessage);

//        ConnectionAttr connectionAttr = ctx.channel().attr(ChannelAttributeKey.CONN_ATTR).get();
        if (true) {
            // 获取session
            if (true) {

            } else {
                log.trace("Unable to process cancel: wrong secret, {} != {}",
                        "s.state.secret",
                        canceMessage.getSecret()
                );
            }
            // todo:取消session
        } else {
            log.trace("Unable to process cancel: unknown session");
        }
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
