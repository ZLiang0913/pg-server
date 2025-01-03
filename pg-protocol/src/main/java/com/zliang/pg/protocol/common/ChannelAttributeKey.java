package com.zliang.pg.protocol.common;

import io.netty.util.AttributeKey;

public interface ChannelAttributeKey {
    AttributeKey<ConnectionAttr> CONN_ATTR = AttributeKey.valueOf("conn_attr");
//    AttributeKey<LoginRequest> LOGIN_REQUEST = AttributeKey.valueOf("login_request");
    AttributeKey<Integer> STORE_INDEX = AttributeKey.valueOf("store_index");
}
