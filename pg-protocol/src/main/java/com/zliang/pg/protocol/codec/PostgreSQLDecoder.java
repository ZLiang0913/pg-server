package com.zliang.pg.protocol.codec;

import com.zliang.pg.common.enums.ErrorCode;
import com.zliang.pg.common.enums.ErrorSeverity;
import com.zliang.pg.common.vo.ErrorResponse;
import com.zliang.pg.protocol.common.ChannelAttributeKey;
import com.zliang.pg.protocol.common.ConnectionAttr;
import com.zliang.pg.protocol.domain.message.InitialMessage;
import com.zliang.pg.protocol.domain.StartupState;
import com.zliang.pg.protocol.pkg.PostgreSQLPacket;
import com.zliang.pg.protocol.pkg.req.*;
import com.zliang.pg.protocol.util.ByteBufUtils;
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
    //client第一次发送的是LoginRequest,后续是Command Phase
    private boolean firstRequest = true;
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        HashMap<String, String> initialParameters;
        StartupState startupState = processInitialMessage(channelHandlerContext, byteBuf);
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
        System.out.println("initialParameters:" + initialParameters);

        /*if (byteBuf.isReadable(4)) {
            int packetSize = byteBuf.getUnsignedMediumLE(0);
            if (byteBuf.isReadable(packetSize + 3*//*Packet Length*//* + 1*//*Packet Num*//*)) {

            } else {
                // data is not full
                log.warn("wait data {}", byteBuf);
                return;
            }
        } else {
            log.warn("no data {}", byteBuf);
            return;
        }
        list.add(decode(byteBuf));*/
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

    public PostgreSQLPacket decode(ByteBuf buf) {
        if (firstRequest) {
            firstRequest = false;
            LoginRequest request = new LoginRequest();
            request.read(buf);
            return request;
        }

        byte commandId = buf.getByte(4);
        PostgreSQLPacket result;
        switch (commandId) {
            case ComQuit.ID:
                ComQuit quit = new ComQuit();
                quit.read(buf);
                result = quit;
                break;
            case ComInitDB.ID:
                ComInitDB initDB = new ComInitDB();
                initDB.read(buf);
                result = initDB;
                break;
            case ComQuery.ID:
                ComQuery query = new ComQuery();
                query.read(buf);
                result = query;
                break;
            case ComFieldList.ID:
                ComFieldList fieldList = new ComFieldList();
                fieldList.read(buf);
                result = fieldList;
                break;
            case ComProcessKill.ID:
                ComProcessKill kill = new ComProcessKill();
                kill.read(buf);
                result = kill;
                break;
            default:
                ComPacket packet = new ComPacket();
                packet.read(buf);
                result = packet;
                break;
        }
        return result;
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
            var error_response = new ErrorResponse(ErrorSeverity.Fatal,
                    ErrorCode.FeatureNotSupported,
                    String.format("unsupported frontend protocol %d.%d: server supports 3.0 to 3.0", startupMessage.getMajor(), startupMessage.getMinor()));
            ByteBuf buffer = ctx.alloc().buffer();
            buffer.writeByte('N');
            ctx.writeAndFlush(buffer);
//            buffer::write_message(&mut self.socket, error_response).await?;
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

//        ConnectionAttr connectionAttr = ctx.channel().attr(ChannelAttributeKey.CONN_ATTR).get();
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
