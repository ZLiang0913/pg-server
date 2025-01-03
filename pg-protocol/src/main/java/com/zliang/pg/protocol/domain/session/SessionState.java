package com.zliang.pg.protocol.domain.session;

import lombok.Data;

import java.sql.PreparedStatement;
import java.util.HashMap;

/**
 * @author 赵亮
 * @version 1.0
 * @description: TODO
 * @date 2024/12/23 15:42
 */
@Data
public class SessionState {
    // connection id, immutable
//    Long connectionId;
    // Can be UUID or anything else. MDX uses UUID
//    String extraId;
    // secret for this session
    Integer secret;
    // client ip, immutable
    String clientIp;
    // client port, immutable
    Integer clientPort;

    // session db variables
//    variables: RwLockSync<Option<DatabaseVariables>>,

    // session temporary tables
//    temp_tables: Arc<TempTableManager>,

//    properties: RwLockSync<SessionProperties>,

    // Context for Transport
//    auth_context: RwLockSync<(Option<AuthContextRef>, SystemTime)>,

//    transaction: RwLockSync<TransactionState>,
//    query: RwLockSync<QueryState>,

    // Extended Query
//    pub statements: RWLockAsync<HashMap<String, PreparedStatement>>,

//    auth_context_expiration: Duration,
}
