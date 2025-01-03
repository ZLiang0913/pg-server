package com.zliang.pg.protocol.domain;

public enum BackendMessageType {
    AuthenticationOk('R'),
    AuthenticationKerberosV5('R'),
    AuthenticationCleartextPassword('R'),
    AuthenticationMD5Password('R'),
    AuthenticationSCMCredential('R'),
    AuthenticationGSS('R'),
    AuthenticationSSPI('R'),
    AuthenticationGSSContinue('R'),
    AuthenticationSASL('R'),
    AuthenticationSASLContinue('R'),
    AuthenticationSASLFinal('R'),
    BackendKeyData('K'),
    BindComplete('2'),
    CloseComplete('3'),
    CommandComplete('C'),
    CopyData('d'),
    CopyDone('c'),
    CopyInResponse('G'),
    CopyOutResponse('H'),
    CopyBothResponse('W'),
    DataRow('D'),
    EmptyQueryResponse('I'),
    ErrorResponse('E'),
    FunctionCallResponse('V'),
    NegotiateProtocolVersion('v'),
    NoData('n'),
    NoticeResponse('N'),
    NotificationResponse('A'),
    ParameterDescription('t'),
    ParameterStatus('S'),
    ParseComplete('1'),
    PortalSuspended('s'),
    ReadyForQuery('Z'),
    RowDescription('T');

    private char id;

    BackendMessageType(char id) {
        this.id = id;
    }

    public char getId() {
        return id;
    }

    public static BackendMessageType fromId(char id) {
        for (BackendMessageType type : BackendMessageType.values()) {
            if (type.id == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid id: " + id);
    }
}