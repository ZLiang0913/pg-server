package com.zliang.pg.protocol.handler;

import com.zliang.pg.common.vo.Portal;
import com.zliang.pg.common.vo.QueryResult;
import com.zliang.pg.protocol.domain.BackendMessage;
import com.zliang.pg.protocol.domain.FrontendMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


// 实现IPostgresFrontendMessageHandler接口来处理不同类型的前端消息
public class ExamplePostgresFrontendMessageHandler implements IPostgresFrontendMessageHandler {
    QueryExecutor queryExecutor = new QueryExecutor();

    private static final Logger logger = LoggerFactory.getLogger(ExamplePostgresFrontendMessageHandler.class);

    // TODO: 重构以使用 "BackendStatus.SSLSupported"/"BackendStatus.SSLNotSupported" 类
    @Override
    public void handleSSLRequest(ChannelHandlerContext ctx, FrontendMessage.SSLRequest msg) {
        logger.debug("SSLRequest");
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeByte('N');
        ctx.writeAndFlush(buffer);
    }

    @Override
    public void handleStartup(ChannelHandlerContext ctx, FrontendMessage.Startup msg) {
        logger.debug("Startup message seen: " + msg);
        ctx.write(new BackendMessage.AuthenticationOk());
        // 发送服务器参数
        ctx.write(new BackendMessage.ParameterStatus("server_version", "14.0"));
        ctx.write(new BackendMessage.BackendKeyData(1, 2));
        ctx.write(new BackendMessage.ReadyForQuery(BackendMessage.ReadyForQuery.TransactionStatus.IDLE));
        ctx.flush();

    }

    @Override
    public void handleQuery(ChannelHandlerContext ctx, FrontendMessage.Query msg) {
        logger.debug("Query message seen: " + msg);
        QueryResult result = queryExecutor.execute(msg.query());
        if (result.getCode() == 200) {
            if (result.getData().isEmpty() && result.getCommand() == "SET") {
                ctx.write(new BackendMessage.CommandComplete(0, BackendMessage.CommandComplete.CommandType.SET));
            } else if (result.getData().isEmpty()) {
                ctx.write(new BackendMessage.NoData());
                ctx.write(new BackendMessage.CommandComplete(0, BackendMessage.CommandComplete.CommandType.SELECT));
            } else {
                // 有数据的结果集
                List<BackendMessage.RowDescription.Field> fields = convertRowDescription(result.getData());
                ctx.write(new BackendMessage.RowDescription(fields));
                List<Object> resultList = result.getData().stream()
                        .flatMap(map -> map.values().stream())
                        .collect(Collectors.toList());
                ctx.write(new BackendMessage.DataRow(resultList));
                ctx.write(new BackendMessage.CommandComplete(1, BackendMessage.CommandComplete.CommandType.SELECT));
            }
        } else if (result.getCode() == 500) {
            ctx.write(new BackendMessage.ErrorResponse((result.getMessage())));
        }
        ctx.write(new BackendMessage.ReadyForQuery(BackendMessage.ReadyForQuery.TransactionStatus.IDLE));
        ctx.flush();
    }

    @Override
    public void handleParse(ChannelHandlerContext ctx, FrontendMessage.Parse msg) {
        logger.debug("Parse message seen: " + msg);
        String query = msg.name();
        // 预执行查询并缓存结果
        QueryResult result = queryExecutor.execute(query);
        msg.portal().setResult(result);
        ctx.write(new BackendMessage.ParseComplete());
        ctx.flush();
    }

    @Override
    public void handleBind(ChannelHandlerContext ctx, FrontendMessage.Bind msg) {
        logger.debug("Bind message seen: " + msg);
        ctx.write(new BackendMessage.BindComplete());
        ctx.flush();
    }

    @Override
    public void handleExecute(ChannelHandlerContext ctx, FrontendMessage.Execute msg) {
        logger.debug("Execute message seen: " + msg);
        Portal portal = msg.portal();
        QueryResult result = portal.getResult();

        //todo:先照搬之前代码
        if (result.getCode() == 200) {
            if (!result.getCommand().startsWith("SET")) {
                if (result.getData() != null && !result.getData().isEmpty()) {
                    List<Object> resultList = result.getData().stream()
                            .flatMap(map -> map.values().stream())
                            .collect(Collectors.toList());
                    ctx.write(new BackendMessage.DataRow(resultList));
                }
                ctx.write(new BackendMessage.CommandComplete(1, BackendMessage.CommandComplete.CommandType.SELECT));
            } else {
                ctx.write(new BackendMessage.CommandComplete(0, BackendMessage.CommandComplete.CommandType.SET));
            }
        } else if (result.getCode() == 500) {
            ctx.write(new BackendMessage.ErrorResponse(result.getMessage()));
        } else {
            ctx.write(new BackendMessage.CommandComplete(0, BackendMessage.CommandComplete.CommandType.SET));
        }
        ctx.flush();
    }

    @Override
    public void handleSync(ChannelHandlerContext ctx, FrontendMessage.Sync msg) {
        logger.debug("Sync message seen: " + msg);
        ctx.writeAndFlush(new BackendMessage.ReadyForQuery(BackendMessage.ReadyForQuery.TransactionStatus.IDLE));
    }

    @Override
    public void handleDescribe(ChannelHandlerContext ctx, FrontendMessage.Describe msg) {
        logger.debug("Describe message seen: " + msg);
        Portal portal = msg.portal();
        QueryResult result = portal.getResult();
        if (result.getCode() == 200) {
            if (result.getData().isEmpty() || result.getCommand().startsWith("SET")) {
                ctx.write(new BackendMessage.NoData());
            } else {
                List<BackendMessage.RowDescription.Field> fields = convertRowDescription(result.getData());
                ctx.write(new BackendMessage.RowDescription(fields));
            }
        } else if (result.getCode() == 500) {
            ctx.write(new BackendMessage.ErrorResponse(result.getMessage()));
        } else {
            ctx.write(new BackendMessage.NoData());
        }
        ctx.flush();
    }

    private List<BackendMessage.RowDescription.Field> convertRowDescription(List<Map<String, Object>> data) {
        if (data == null || data.isEmpty()) return Collections.emptyList();
        Map<String, Object> dataMap = data.get(0);
        List<BackendMessage.RowDescription.Field> result = dataMap.entrySet().stream().map(entry -> {
            String name = entry.getKey();
            Object value = entry.getValue();
            return switch (value) {
                case Integer i -> new BackendMessage.RowDescription.Int4Field(name);
                case String s -> new BackendMessage.RowDescription.TextField(name);
                case Boolean b -> new BackendMessage.RowDescription.BooleanField(name);
                default -> new BackendMessage.RowDescription.TextField(name);
            };
        }).toList();
        return result;
    }

    @Override
    public void handleClose(ChannelHandlerContext ctx, FrontendMessage.Close msg) {
        logger.debug("Send CloseComplete");
        ctx.writeAndFlush(new BackendMessage.CloseComplete());
    }

}