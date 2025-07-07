package com.zliang.pg.protocol.handler;

import com.zliang.pg.protocol.domain.FrontendMessage;
import io.netty.channel.ChannelHandlerContext;

// 定义一个接口，用于处理不同类型的前端消息
public interface IPostgresFrontendMessageHandler {

    // 处理SSL请求消息的方法
    void handleSSLRequest(ChannelHandlerContext ctx, FrontendMessage.SSLRequest msg);

    // 处理启动消息的方法
    void handleStartup(ChannelHandlerContext ctx, FrontendMessage.Startup msg);

    // 处理查询消息的方法
    void handleQuery(ChannelHandlerContext ctx, FrontendMessage.Query msg);

    void handleParse(ChannelHandlerContext ctx, FrontendMessage.Parse msg);

    void handleBind(ChannelHandlerContext ctx, FrontendMessage.Bind msg);

    void handleExecute(ChannelHandlerContext ctx, FrontendMessage.Execute msg);

    void handleSync(ChannelHandlerContext ctx, FrontendMessage.Sync msg);

    void handleDescribe(ChannelHandlerContext ctx, FrontendMessage.Describe msg);

    void handleClose(ChannelHandlerContext ctx, FrontendMessage.Close msg);
}