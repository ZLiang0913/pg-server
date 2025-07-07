package com.zliang.pg.protocol.handler;

import com.zliang.pg.common.vo.QueryResult;
import lombok.val;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;

/**
 * @author 赵亮
 * @version 1.0
 * @description: TODO
 * @date 2024/12/5 9:11
 */
public class QueryExecutor {
    QueryResult execute(String query) {
        System.out.println("Executing query: " + query);

        try {
            val normalizedQuery = query.trim().toLowerCase();
            if (normalizedQuery.startsWith("set ")) {
                return QueryResult.success(emptyList(), "SET");
            } else if (Objects.equals(normalizedQuery, "select version()")) {
                Map map = new HashMap() {
                    {
                        put("version", "PostgreSQL 14.0 on LOUIS-PC");
                    }
                };
                return QueryResult.success(Arrays.asList(map), "SELECT 1");
            } else if (Objects.equals(normalizedQuery, "select 'keep alive'")) {
                Map map = new HashMap() {
                    {
                        put("?column?", "keep alive");
                    }
                };
                return QueryResult.success(Arrays.asList(map), "SELECT 1");
            } else if (normalizedQuery.contains("pg_stat_ssl")) {
                Map map = new HashMap() {
                    {
                        put("ssl", "f");
                    }
                };
                return QueryResult.success(Arrays.asList(map), "SELECT 1");
            } else if (normalizedQuery.startsWith("select ")) {
                Pattern pattern = Pattern.compile("SELECT\\s+\\*\\s+FROM\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(normalizedQuery);
                while (matcher.find()) {
                    String tableName = matcher.group(1);
//                    List data = makeApiCall(tableName);
                    List data = new ArrayList();
                    data.add(Map.of("table", tableName));
                    return QueryResult.success(data, "SELECT " + data.size());
                }
            }

            return QueryResult.success(emptyList(), "SELECT 0");
        } catch (Exception e) {
            System.out.println("Query execution error: " + e.getMessage());
            e.printStackTrace();
            return QueryResult.error("Error executing query: " + e.getMessage());
        }
    }

    /*private List makeApiCall(String tableName) {
        // 创建 HttpClient 实例
        HttpClient client = HttpClient.newHttpClient();

        // 构建 HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3011/test/playground/" + tableName))
                .header("Content-Type", "application/json") // 设置请求头
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        // 发送请求并接收响应
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // 处理响应
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Headers: " + response.headers());
            System.out.println("Response Body: " + response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 处理响应
        if (response != null && response.statusCode() == 200) {
            return JSON.parseArray(response.body(), Map.class);
        } else {
            // 处理错误响应
            System.out.println("Error Response: " + response.body().toString());
            return new ArrayList();
        }
    }*/
}
