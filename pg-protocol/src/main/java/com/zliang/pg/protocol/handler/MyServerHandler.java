package com.zliang.pg.protocol.handler;

import com.zliang.pg.protocol.common.ChannelAttributeKey;
import com.zliang.pg.protocol.common.ConnectionAttr;
import com.zliang.pg.protocol.domain.session.Session;
import com.zliang.pg.protocol.domain.session.SessionState;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * @author 赵亮
 * @version 1.0
 * @description: TODO
 * @date 2024/12/23 14:12
 */
@Slf4j
@ChannelHandler.Sharable
public class MyServerHandler extends ChannelInboundHandlerAdapter {
    private static final AtomicLong ID = new AtomicLong(1);
    private final Random random = new Random();

    /**
     * 当一个新的连接被建立，也就是一个Channel（通道）变为活跃状态时，channelActive方法会被调用。通过重写这个方法可以在客户端连接到服务器时执行特定的操作。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
//        log.info("New ConnectionId:{}, From {}", channel.id(), channel.remoteAddress());
        Session session = createSession(channel);
        ConnectionAttr attr = ConnectionAttr.builder().connectionId(channel.id().asShortText()).session(session).build();
        channel.attr(ChannelAttributeKey.CONN_ATTR).set(attr);
    }

    @SneakyThrows
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ConnectionAttr attr = ctx.channel().attr(ChannelAttributeKey.CONN_ATTR).get();
        log.info("Accept ConnectionId: {}, {}", attr.getConnectionId(), msg);
    }

    private Session createSession(Channel channel) {
        SessionState state = new SessionState();
//        state.setConnectionId(ID.getAndIncrement());
        state.setSecret(random.nextInt());
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        state.setClientIp(remoteAddress.getAddress().getHostAddress());
        state.setClientPort(remoteAddress.getPort());
        return new Session(state);
    }
}
