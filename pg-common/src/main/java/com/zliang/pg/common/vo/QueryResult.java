package com.zliang.pg.common.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QueryResult {
    private long code;
    private String message;
    private String command;
    private List<Map<String, Object>> data;

    protected QueryResult(long code, String message, String command, List<Map<String, Object>> data) {
        this.code = code;
        this.message = message;
        this.command = command;
        this.data = data;
    }

    public static <T> QueryResult success(List<Map<String, Object>> data, String command) {
        return new QueryResult(200, "", command, data);
    }

    public static <T> QueryResult error(String message) {
        return new QueryResult(500, message, "", null);
    }
}