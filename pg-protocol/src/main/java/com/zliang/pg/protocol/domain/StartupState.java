package com.zliang.pg.protocol.domain;

import java.util.HashMap;

public interface StartupState {
    record Success(HashMap<String, String> parameters) implements StartupState {

    }

    record SslRequested() implements StartupState {

    }

    record Denied() implements StartupState {

    }

    record CancelRequest() implements StartupState {

    }
}
