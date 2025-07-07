package com.zliang.pg.protocol.handler;

import com.zliang.pg.protocol.domain.FrontendMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 实现SimpleChannelInboundHandler来处理前端消息，并将处理逻辑委托给IPostgresFrontendMessageHandler接口实现类
public class DelegatingPostgresFrontendMessageHandler extends SimpleChannelInboundHandler<FrontendMessage> {

    private static final Logger logger = LoggerFactory.getLogger(DelegatingPostgresFrontendMessageHandler.class);

    private final IPostgresFrontendMessageHandler handler;

    // 通过构造函数传入IPostgresFrontendMessageHandler实现类
    public DelegatingPostgresFrontendMessageHandler(IPostgresFrontendMessageHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FrontendMessage msg) {
        logger.debug("Message received: " + msg);
        // 根据消息类型将消息分发给委托的接口实现类进行处理
        switch (msg) {
            case FrontendMessage.SSLRequest ssl -> handler.handleSSLRequest(ctx, ssl);
            case FrontendMessage.Startup startup -> handler.handleStartup(ctx, startup);
            case FrontendMessage.Query query -> handler.handleQuery(ctx, query);
            case FrontendMessage.Parse query -> handler.handleParse(ctx, query);
            case FrontendMessage.Bind bind -> handler.handleBind(ctx, bind);
            case FrontendMessage.Execute execute -> handler.handleExecute(ctx, execute);
            case FrontendMessage.Sync sync -> handler.handleSync(ctx, sync);
            case FrontendMessage.Describe describe -> handler.handleDescribe(ctx, describe);
            case FrontendMessage.Close close -> handler.handleClose(ctx, close);
            default -> throw new IllegalStateException("Unknown message type: " + msg);
        }
    }

}