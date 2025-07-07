package com.zliang.pg.protocol.domain;


import com.zliang.pg.common.vo.Portal;

import java.util.Map;

public sealed interface FrontendMessage permits
        FrontendMessage.SSLRequest,
        FrontendMessage.Startup,
        FrontendMessage.Bind,
        FrontendMessage.Close,
        FrontendMessage.CopyData,
        FrontendMessage.CopyDone,
        FrontendMessage.CopyFail,
        FrontendMessage.Describe,
        FrontendMessage.Execute,
        FrontendMessage.Flush,
        FrontendMessage.FunctionCall,
        FrontendMessage.Parse,
        FrontendMessage.Password,
        FrontendMessage.Query,
        FrontendMessage.Sync,
        FrontendMessage.Terminate {
    Character getId();

    record SSLRequest() implements FrontendMessage {
        @Override
        public Character getId() {
            return null;
        }
    }

    record Startup(int version, Map<String, String> parameters) implements FrontendMessage {
        @Override
        public Character getId() {
            return null;
        }
    }

    record Bind(String name/*, Map<String, String> parameters*/) implements FrontendMessage {

        @Override
        public Character getId() {
            return 'B';
        }
    }

    record Close() implements FrontendMessage {

        @Override
        public Character getId() {
            return 'C';
        }
    }

    record CopyData(byte[] data) implements FrontendMessage {

        @Override
        public Character getId() {
            return 'd';
        }
    }

    record CopyDone() implements FrontendMessage {

        @Override
        public Character getId() {
            return 'c';
        }
    }

    record CopyFail() implements FrontendMessage {

        @Override
        public Character getId() {
            return 'f';
        }
    }

    record Describe(String name, Portal portal) implements FrontendMessage {

        @Override
        public Character getId() {
            return 'D';
        }
    }

    record Execute(String name, Portal portal/*, Map<String, String> parameters*/) implements FrontendMessage {

        @Override
        public Character getId() {
            return 'E';
        }
    }

    record Flush() implements FrontendMessage {

        @Override
        public Character getId() {
            return 'H';
        }
    }

    record FunctionCall(String name, Map<String, String> parameters) implements FrontendMessage {

        @Override
        public Character getId() {
            return 'F';
        }
    }

    record Parse(String name, Portal portal/*, Map<String, String> parameters*/) implements FrontendMessage {

        @Override
        public Character getId() {
            return 'P';
        }
    }

    record Password(String password) implements FrontendMessage {

        @Override
        public Character getId() {
            return 'p';
        }
    }

    record Query(String query) implements FrontendMessage {
        @Override
        public Character getId() {
            return 'Q';
        }
    }

    record Sync() implements FrontendMessage {

        @Override
        public Character getId() {
            return 'S';
        }
    }

    record Terminate() implements FrontendMessage {

        @Override
        public Character getId() {
            return 'X';
        }
    }

}