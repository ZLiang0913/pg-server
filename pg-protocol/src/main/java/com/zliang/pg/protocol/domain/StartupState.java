package com.zliang.pg.protocol.domain;

import com.zliang.pg.protocol.enums.AuthenticationRequest;

import java.util.HashMap;

public interface StartupState {
    record Success(HashMap<String, String> parameters, AuthenticationRequest authMethod) implements StartupState {

    }

    record SslRequested() implements StartupState {

    }

    record Denied() implements StartupState {

    }

    record CancelRequest() implements StartupState {

    }
}
