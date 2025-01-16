package com.zliang.pg.protocol.enums;

public enum FrontendMessageType {
    Startup(null),
    Bind('B'),
    Close('C'),
    CopyData('d'),
    CopyDone('c'),
    CopyFail('f'),
    Describe('D'),
    Execute('E'),
    Flush('H'),
    FunctionCall('F'),
    Parse('P'),
    Password('p'),
    Query('Q'),
    Sync('S'),
    Terminate('X');

    private Character id;

    FrontendMessageType(Character id) {
        this.id = id;
    }

    public Character getId() {
        return id;
    }

    public static FrontendMessageType fromId(char id) {
        for (FrontendMessageType type : FrontendMessageType.values()) {
            if (type.id!= null && type.id.charValue() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid id: " + id);
    }
}