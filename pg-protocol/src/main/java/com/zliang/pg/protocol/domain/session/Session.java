package com.zliang.pg.protocol.domain.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 赵亮
 * @version 1.0
 * @description: TODO
 * @date 2024/12/23 15:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {
//    SessionManager sessionManager;
//    ServerManager server;
    SessionState state;
}
