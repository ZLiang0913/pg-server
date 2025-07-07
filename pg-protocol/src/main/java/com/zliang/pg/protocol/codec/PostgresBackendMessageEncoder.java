package com.zliang.pg.protocol.codec;

import com.zliang.pg.common.util.ByteBufUtils;
import com.zliang.pg.protocol.domain.BackendMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

// Sharable because it's stateless -- we can re-use the same instance for every message
@ChannelHandler.Sharable
public class PostgresBackendMessageEncoder extends MessageToByteEncoder<BackendMessage> {

    private static final Logger logger = LoggerFactory.getLogger(PostgresBackendMessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, BackendMessage msg, ByteBuf out) {
        out.writeByte((int) msg.getId());
        if (msg instanceof BackendMessage.AuthenticationOk) {
            logger.debug("AuthenticationOk");
            out.writeInt(8); // length
            out.writeInt(0); // success
        } else if (msg instanceof BackendMessage.BackendKeyData) {
            logger.debug("BackendKeyData");
            out.writeInt(12); // length
            BackendMessage.BackendKeyData backendKeyData = (BackendMessage.BackendKeyData) msg;
            out.writeInt(backendKeyData.processId());
            out.writeInt(backendKeyData.secretKey());
        } else if (msg instanceof BackendMessage.ParameterStatus) {
            logger.debug("ParameterStatus");
            BackendMessage.ParameterStatus parameterStatus = (BackendMessage.ParameterStatus) msg;
            String name = parameterStatus.name();
            String value = parameterStatus.data();
            out.writeInt(4 + name.length() + 1 + value.length() + 1); // length
            ByteBufUtils.writeCString(out, name);
            ByteBufUtils.writeCString(out, value);
        } else if (msg instanceof BackendMessage.ReadyForQuery) {
            logger.debug("ReadyForQuery");
            out.writeInt(5); // length
            BackendMessage.ReadyForQuery readyForQuery = (BackendMessage.ReadyForQuery) msg;
            out.writeByte((int) readyForQuery.transactionStatus().getValue());
        } else if (msg instanceof BackendMessage.RowDescription) {
            logger.debug("RowDescription");
            int length = 0;

            out.writeInt(0); // Will be modified after all fields are written
            length += 4;

            BackendMessage.RowDescription rowDescription = (BackendMessage.RowDescription) msg;
            List<BackendMessage.RowDescription.Field> fields = rowDescription.fields();
            out.writeShort(fields.size());
            length += 2;

            for (BackendMessage.RowDescription.Field it : fields) {
                ByteBufUtils.writeCString(out, it.getName());
                length += it.getName().length() + 1; // Add 1 for the null terminator

                out.writeInt(it.getTableOid());
                length += 4;

                out.writeShort(it.getColumnIdx());
                length += 2;

                out.writeInt(it.getDataTypeOid());
                length += 4;

                out.writeShort(it.getDataTypeSize());
                length += 2;

                out.writeInt(it.getDataTypeModifier());
                length += 4;

                out.writeShort(it.getFormat().getId());
                length += 2;
            }

            // Update length of message with calculated length
            out.setInt(1, length);
        } else if (msg instanceof BackendMessage.DataRow) {
            logger.debug("DataRow");

            // the following pair of fields appear for each column:
            //
            //  Int32
            //  The length of the column value, in bytes (this count does not include itself). Can be zero. As a special case, -1 indicates a NULL column value. No value bytes follow in the NULL case.
            //
            //  Byten
            //  The absolute value of the column, in the format indicated by the associated format code. n is the above length.
            ByteBuf rowBuffer = Unpooled.buffer();
            // TODO: Currently only handles "TEXT" mode, not "BINARY"
            BackendMessage.DataRow dataRow = (BackendMessage.DataRow) msg;
            List<Object> columns = dataRow.columns();
            for (Object it : columns) {
                String colValueAsString;
                if (it instanceof String) {
                    colValueAsString = (String) it;
                } else if (it instanceof Character) {
                    colValueAsString = it.toString();
                } else if (it instanceof byte[]) {
                    colValueAsString = new String((byte[]) it, StandardCharsets.UTF_8);
                } else if (it instanceof Number) {
                    colValueAsString = it.toString();
                } else if (it instanceof Boolean) {
                    colValueAsString = ((Boolean) it) ? "t" : "f";
                } else {
                    throw new IllegalArgumentException("Unsupported column type: " + it.getClass().getName());
                }
                int writerIndex = rowBuffer.writerIndex();
                rowBuffer.writeInt(0);
                int bytesWritten = rowBuffer.writeCharSequence(colValueAsString, StandardCharsets.UTF_8);
                rowBuffer.setInt(writerIndex, bytesWritten);
            }

            int rowLength = rowBuffer.readableBytes();
            logger.debug("Row length: " + rowLength);

            // Int32 - Length of message contents in bytes, including self.
            int totalLength = rowLength + 4 + 2; // 4 for length, 2 for column count
            logger.debug("DataRow total length: " + totalLength);

            out.writeInt(totalLength);
            out.writeShort(columns.size());
            out.writeBytes(rowBuffer);
        } else if (msg instanceof BackendMessage.CommandComplete) {
            logger.debug("CommandComplete");

            BackendMessage.CommandComplete commandComplete = (BackendMessage.CommandComplete) msg;
            String command;
            BackendMessage.CommandComplete.CommandType type = commandComplete.type();
            if (type == BackendMessage.CommandComplete.CommandType.INSERT) {
                command = "INSERT 0 " + commandComplete.affectedRows();
            } else if (type == BackendMessage.CommandComplete.CommandType.UPDATE) {
                command = "UPDATE " + commandComplete.affectedRows();
            } else if (type == BackendMessage.CommandComplete.CommandType.DELETE) {
                command = "DELETE " + commandComplete.affectedRows();
            } else if (type == BackendMessage.CommandComplete.CommandType.SELECT) {
                command = "SELECT " + commandComplete.affectedRows();
            } else if (type == BackendMessage.CommandComplete.CommandType.COPY) {
                command = "COPY " + commandComplete.affectedRows();
            } else if (type == BackendMessage.CommandComplete.CommandType.MOVE) {
                command = "MOVE " + commandComplete.affectedRows();
            } else if (type == BackendMessage.CommandComplete.CommandType.FETCH) {
                command = "FETCH " + commandComplete.affectedRows();
            } else if (type == BackendMessage.CommandComplete.CommandType.SET) {//后加的
                command = "SET";
            } else {
                throw new IllegalArgumentException("Invalid CommandType: " + type);
            }

            byte[] commandBytes = command.getBytes(Charset.forName("UTF-8"));
            int commandStrSize = commandBytes.length + 1; // +1 for null terminator
            out.writeInt(commandStrSize + 4); // length
            ByteBufUtils.writeCString(out, command);
        } else if (msg instanceof BackendMessage.ParseComplete) {
            logger.debug("ParseComplete");
            out.writeInt(4);
        } else if (msg instanceof BackendMessage.BindComplete) {
            logger.debug("BindComplete");
            out.writeInt(4);
        } else if (msg instanceof BackendMessage.ErrorResponse) {
            logger.debug("ErrorResponse");
            BackendMessage.ErrorResponse errorResponse = (BackendMessage.ErrorResponse) msg;
            val messageBytes = errorResponse.data().getBytes();
            out.writeInt(4 + 1 + messageBytes.length + 1 + 1);
            out.writeByte('M');
            out.writeBytes(messageBytes);
            out.writeByte(0);
            out.writeByte(0);
        } else if (msg instanceof BackendMessage.NoData) {
            out.writeInt(4);
        } else if (msg instanceof BackendMessage.CloseComplete) {
            out.writeInt(4);
        } else {
            throw new UnsupportedOperationException("Unsupported message: " + msg);
        }
    }
}