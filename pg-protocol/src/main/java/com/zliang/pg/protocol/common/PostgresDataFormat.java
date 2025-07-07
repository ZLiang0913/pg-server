package com.zliang.pg.protocol.common;

public enum PostgresDataFormat {
    TEXT(0),
    BINARY(1);

    private int id;

    PostgresDataFormat(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static PostgresDataFormat fromId(int id) {
        if (id == 0) {
            return TEXT;
        } else if (id == 1) {
            return BINARY;
        } else {
            throw new IllegalArgumentException("Unknown PostgresDataFormat id: " + id);
        }
    }
}