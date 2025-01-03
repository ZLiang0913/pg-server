package com.zliang.pg.protocol.config;

import io.netty.util.internal.SystemPropertyUtil;

/**
 * @author 赵亮
 * @version 1.0
 * @description: 服务端参数配置
 * @date 2024/12/31 11:01
 */
public class ServerConfig {
    public static final int QUERY_TIMEOUT = SystemPropertyUtil.getInt("QUERY_TIMEOUT", 120);
    public static final String TIMEZONE = "UTC";
    public static final boolean DISABLE_STRICT_AGG_TYPE_MATCH = SystemPropertyUtil.getBoolean("DISABLE_STRICT_AGG_TYPE_MATCH", false);
    public static final int AUTH_EXPIRE_SECS = SystemPropertyUtil.getInt("AUTH_EXPIRE_SECS", 120);
    public static final int COMPILER_CACHE_SIZE = SystemPropertyUtil.getInt("COMPILER_CACHE_SIZE", 100);
    public static final int QUERY_CACHE_SIZE = SystemPropertyUtil.getInt("QUERY_CACHE_SIZE", 500);
    public static final boolean SQL_PUSH_DOWN = SystemPropertyUtil.getBoolean("SQL_PUSH_DOWN", true);
    public static final boolean STREAM_MODE = SystemPropertyUtil.getBoolean("STREAM_MODE", false);
    public static final int NON_STREAMING_QUERY_MAX_ROW_LIMIT = SystemPropertyUtil.getInt("DB_QUERY_LIMIT", 50000);
    public static final int MAX_SESSIONS = SystemPropertyUtil.getInt("MAX_SESSIONS", 1024);
    public static final boolean NO_IMPLICIT_ORDER = SystemPropertyUtil.getBoolean("SQL_NO_IMPLICIT_ORDER", true);
    public static final boolean TOP_DOWN_EXTRACTOR = SystemPropertyUtil.getBoolean("TOP_DOWN_EXTRACTOR", true);
}
