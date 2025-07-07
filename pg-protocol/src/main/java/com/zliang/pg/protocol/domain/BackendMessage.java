package com.zliang.pg.protocol.domain;

import com.zliang.pg.protocol.common.PgTypeTable;
import com.zliang.pg.protocol.common.PostgresDataFormat;
import lombok.Data;

import java.util.List;

public sealed interface BackendMessage permits
        BackendMessage.AuthenticationOk,
        BackendMessage.AuthenticationKerberosV5,
        BackendMessage.AuthenticationCleartextPassword,
        BackendMessage.AuthenticationMD5Password,
        BackendMessage.AuthenticationSCMCredential,
        BackendMessage.AuthenticationGSS,
        BackendMessage.AuthenticationSSPI,
        BackendMessage.AuthenticationGSSContinue,
        BackendMessage.AuthenticationSASL,
        BackendMessage.AuthenticationSASLContinue,
        BackendMessage.AuthenticationSASLFinal,
        BackendMessage.BackendKeyData,
        BackendMessage.BindComplete,
        BackendMessage.CloseComplete,
        BackendMessage.CommandComplete,
        BackendMessage.CopyData,
        BackendMessage.CopyDone,
        BackendMessage.CopyInResponse,
        BackendMessage.CopyOutResponse,
        BackendMessage.CopyBothResponse,
        BackendMessage.DataRow,
        BackendMessage.EmptyQueryResponse,
        BackendMessage.ErrorResponse,
        BackendMessage.FunctionCallResponse,
        BackendMessage.NegotiateProtocolVersion,
        BackendMessage.NoData,
        BackendMessage.NoticeResponse,
        BackendMessage.NotificationResponse,
        BackendMessage.ParameterDescription,
        BackendMessage.ParameterStatus,
        BackendMessage.ParseComplete,
        BackendMessage.PortalSuspended,
        BackendMessage.ReadyForQuery,
        BackendMessage.RowDescription {

    char getId();

    // 以下是各个具体的内部静态类，对应Kotlin中的嵌套类

    record AuthenticationOk() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.AuthenticationOk.getId();
        }
    }

    record AuthenticationKerberosV5() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.AuthenticationKerberosV5.getId();
        }
    }

    record AuthenticationCleartextPassword() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.AuthenticationCleartextPassword.getId();
        }
    }

    record AuthenticationMD5Password(byte[] salt) implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.AuthenticationMD5Password.getId();
        }
    }

    record AuthenticationSCMCredential() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.AuthenticationSCMCredential.getId();
        }
    }

    record AuthenticationGSS() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.AuthenticationGSS.getId();
        }
    }

    record AuthenticationSSPI() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.AuthenticationSSPI.getId();
        }
    }

    record AuthenticationGSSContinue() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.AuthenticationGSSContinue.getId();
        }
    }

    record AuthenticationSASL() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.AuthenticationSASL.getId();
        }
    }

    record AuthenticationSASLContinue() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.AuthenticationSASLContinue.getId();
        }
    }

    record AuthenticationSASLFinal() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.AuthenticationSASLFinal.getId();
        }
    }

    record BackendKeyData(int processId, int secretKey) implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.BackendKeyData.getId();
        }
    }

    record BindComplete() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.BindComplete.getId();
        }
    }

    record CloseComplete() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.CloseComplete.getId();
        }
    }

    record CommandComplete(int affectedRows, CommandType type) implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.CommandComplete.getId();
        }

        public enum CommandType {
            SELECT,
            INSERT,
            UPDATE,
            DELETE,
            MOVE,
            FETCH,
            COPY,
            SET
        }
    }

    record CopyData(byte[] data) implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.CopyData.getId();
        }
    }

    record CopyDone() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.CopyDone.getId();
        }
    }

    record CopyInResponse() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.CopyInResponse.getId();
        }
    }

    record CopyOutResponse() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.CopyOutResponse.getId();
        }
    }

    record CopyBothResponse() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.CopyBothResponse.getId();
        }
    }

    record DataRow(List<Object> columns) implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.DataRow.getId();
        }
    }

    record EmptyQueryResponse() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.EmptyQueryResponse.getId();
        }
    }

    record ErrorResponse(String data) implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.ErrorResponse.getId();
        }
    }

    record FunctionCallResponse(String data) implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.FunctionCallResponse.getId();
        }
    }

    record NegotiateProtocolVersion(int data) implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.NegotiateProtocolVersion.getId();
        }
    }

    record NoData() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.NoData.getId();
        }
    }

    record NoticeResponse(String data) implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.NoticeResponse.getId();
        }
    }

    record NotificationResponse(String data) implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.NotificationResponse.getId();
        }
    }

    record ParameterDescription(List<String> data) implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.ParameterDescription.getId();
        }
    }

    record ParameterStatus(String name, String data) implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.ParameterStatus.getId();
        }
    }

    record ParseComplete() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.ParseComplete.getId();
        }
    }

    record PortalSuspended() implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.PortalSuspended.getId();
        }
    }

    record ReadyForQuery(TransactionStatus transactionStatus) implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.ReadyForQuery.getId();
        }

        public enum TransactionStatus {
            IDLE('I'),
            STARTED('T'),
            FAILED('E');

            private char value;

            TransactionStatus(char value) {
                this.value = value;
            }

            public char getValue() {
                return value;
            }
        }
    }

    record RowDescription(List<Field> fields) implements BackendMessage {
        @Override
        public char getId() {
            return BackendMessageType.RowDescription.getId();
        }

        @Data
        public static class Field {
            private String name;
            private int dataTypeOid;
            private int dataTypeSize;
            private int dataTypeModifier;
            private int tableOid;
            private int columnIdx;
            private PostgresDataFormat format;

            public Field(String name, int dataTypeOid, int dataTypeSize, int dataTypeModifier, int tableOid, int columnIdx, PostgresDataFormat format) {
                this.name = name;
                this.dataTypeOid = dataTypeOid;
                this.dataTypeSize = dataTypeSize;
                this.dataTypeModifier = dataTypeModifier;
                this.tableOid = tableOid;
                this.columnIdx = columnIdx;
                this.format = format;
            }
        }

        // 以下是各个具体的Field子类，对应Kotlin中的嵌套类

        public static class TextField extends Field {
            public TextField(String name) {
                super(name, PgTypeTable.text.getOid(), PgTypeTable.text.getByteLength(), -1, 0, 0, PostgresDataFormat.TEXT);
            }
        }

        public static class CharField extends Field {
            public CharField(String name, int tableOid, int columnIdx, PostgresDataFormat format) {
                super(name, PgTypeTable.chara.getOid(), PgTypeTable.chara.getByteLength(), -1, tableOid, columnIdx, format);
            }
        }

        public static class DateField extends Field {
            public DateField(String name, int tableOid, int columnIdx, PostgresDataFormat format) {
                super(name, PgTypeTable.date.getOid(), PgTypeTable.date.getByteLength(), -1, tableOid, columnIdx, format);
            }
        }

        public static class TimeField extends Field {
            public TimeField(String name, int tableOid, int columnIdx, PostgresDataFormat format) {
                super(name, PgTypeTable.time.getOid(), PgTypeTable.time.getByteLength(), -1, tableOid, columnIdx, format);
            }
        }

        public static class TimestampField extends Field {
            public TimestampField(String name, int tableOid, int columnIdx, PostgresDataFormat format) {
                super(name, PgTypeTable.timestamp.getOid(), PgTypeTable.timestamp.getByteLength(), -1, tableOid, columnIdx, format);
            }
        }

        public static class TimestamptzField extends Field {
            public TimestamptzField(String name, int tableOid, int columnIdx, PostgresDataFormat format) {
                super(name, PgTypeTable.timestamptz.getOid(), PgTypeTable.timestamptz.getByteLength(), -1, tableOid, columnIdx, format);
            }
        }

        public static class IntervalField extends Field {
            public IntervalField(String name, int tableOid, int columnIdx, PostgresDataFormat format) {
                super(name, PgTypeTable.interval.getOid(), PgTypeTable.interval.getByteLength(), -1, tableOid, columnIdx, format);
            }
        }


        public static class NumericField extends Field {
            public NumericField(String name) {
                super(name, PgTypeTable.numeric.getOid(), PgTypeTable.numeric.getByteLength(), -1, 0, 0, PostgresDataFormat.TEXT);
            }
        }

        public static class Int4Field extends Field {
            public Int4Field(String name) {
                super(name, PgTypeTable.int4.getOid(), PgTypeTable.int4.getByteLength(), -1, 0, 0, PostgresDataFormat.TEXT);
            }
        }

        public static class BooleanField extends Field {
            public BooleanField(String name) {
                super(name, PgTypeTable.bool.getOid(), PgTypeTable.bool.getByteLength(), -1, 0, 0, PostgresDataFormat.TEXT);
            }
        }

    }
}
