package com.zliang.pg.protocol.handler;

import com.zliang.pg.common.exceptions.PgErrorException;
import com.zliang.pg.protocol.common.ConnectionAttr;
import com.zliang.pg.protocol.pkg.backend.ErrorResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author 赵亮
 * @version 1.0
 * @description: TODO
 * @date 2024/12/23 14:12
 */
@Slf4j
@ChannelHandler.Sharable
public class MyServerHandler extends ChannelInboundHandlerAdapter {
    private static final AtomicInteger ID = new AtomicInteger(1);
    public static final AttributeKey<ConnectionAttr> CONN_ATTR = AttributeKey.valueOf("conn_attr");

    /**
     * 当一个新的连接被建立，也就是一个Channel（通道）变为活跃状态时，channelActive方法会被调用。通过重写这个方法可以在客户端连接到服务器时执行特定的操作。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        int id = ID.getAndIncrement();
        log.info("New ConnectionId:{}, From {}", id, ctx.channel().remoteAddress());
        ConnectionAttr attr = ConnectionAttr.builder().connectionId(id).build();
        ctx.channel().attr(CONN_ATTR).set(attr);
        // todo:创建session
    }

    @SneakyThrows
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ConnectionAttr attr = ctx.channel().attr(CONN_ATTR).get();
        log.info("Accept ConnectionId: {}, {}", attr.getConnectionId(), msg);
    }

    /*private Session createSession(Channel channel) {
        SessionState state = new SessionState();
//        state.setConnectionId(ID.getAndIncrement());
        state.setSecret(random.nextInt());
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        state.setClientIp(remoteAddress.getAddress().getHostAddress());
        state.setClientPort(remoteAddress.getPort());
        return new Session(state);
    }*/

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ConnectionAttr attr = ctx.channel().attr(CONN_ATTR).get();
        log.error("ExceptionCaught ConnectionId:{}", attr.getConnectionId(), cause);
        if (cause instanceof PgErrorException error) {
            ErrorResponse errorResponse = new ErrorResponse(error.getSeverity(), error.getCode(), error.getMessage());
            ctx.writeAndFlush(errorResponse);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ConnectionAttr attr = ctx.channel().attr(CONN_ATTR).get();
        log.info("Close ConnectionId:{}", attr.getConnectionId());
//        try {
        ctx.channel().attr(CONN_ATTR).set(null);
//            storageProvider.release();
//        } catch (SQLException exception) {
//            log.error("channelInactive ", exception);
//        }
    }
}
