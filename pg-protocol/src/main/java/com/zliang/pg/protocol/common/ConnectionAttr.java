package com.zliang.pg.protocol.common;

import com.zliang.pg.protocol.domain.session.Session;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConnectionAttr {
    private String connectionId;
    private Session session;
}
