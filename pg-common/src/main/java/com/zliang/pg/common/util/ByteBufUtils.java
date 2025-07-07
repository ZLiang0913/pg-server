package com.zliang.pg.common.util;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteBufUtils {

    /**
     * 读取以null结尾的C字符串
     * @param buffer
     * @return
     */
    public static String readCString(ByteBuf buffer) {
        int length = buffer.bytesBefore((byte) 0);
        if (length == -1) {
            return null;
        }
        byte[] bytes = new byte[length + 1];
        buffer.readBytes(bytes);
        return new String(bytes, 0, length, StandardCharsets.UTF_8);
    }

    /**
     * 读取以null结尾的字符数组
     * @param buffer
     * @return
     */
    public static char[] readCharArray(ByteBuf buffer) {
        int length = buffer.bytesBefore((byte) 0);
        if (length == -1) {
            return null;
        }
        byte[] bytes = new byte[length + 1];
        buffer.readBytes(bytes);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return StandardCharsets.UTF_8.decode(byteBuffer).array();
    }

    /**
     * 写入以null结尾的C字符串
     * @param buffer
     * @param str
     */
    public static void writeCString(ByteBuf buffer, String str) {
        buffer.writeBytes(str.getBytes(StandardCharsets.UTF_8));
        buffer.writeByte(0);
    }


    /**
     * 读取指定长度的字符数组,并返回String类型字符串
     * @param buffer
     * @param length
     * @return
     */
    public static String readString(ByteBuf buffer, int length) {
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return StandardCharsets.UTF_8.decode(byteBuffer).toString();
    }
}