package com.zliang.pg.common.vo;

import com.zliang.pg.common.enums.MessageType;
import lombok.Data;

import java.util.Map;

/**
 * 类说明：消息实体类
 */
@Data
public final class MyMessage {

    private MessageType msgType;

    private Object message;

    /*消息头额外附件*/
    private Map<String, Object> attachment;

    public static MyMessage user(String message) {
        MyMessage msg = new MyMessage();
        msg.setMsgType(MessageType.User);
        msg.setMessage(message);
        return msg;
    }

    public static MyMessage internal(String message) {
        MyMessage msg = new MyMessage();
        msg.setMsgType(MessageType.Internal);
        msg.setMessage(message);
        return msg;
    }

}
