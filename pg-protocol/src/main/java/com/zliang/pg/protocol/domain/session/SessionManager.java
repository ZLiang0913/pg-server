package com.zliang.pg.protocol.domain.session;

/**
 * @author 赵亮
 * @version 1.0
 * @description: TODO
 * @date 2024/12/23 15:42
 */
public class SessionManager {
    ServerManager server;

    public SessionManager(ServerManager server) {
        this.server = server;
    }
}
