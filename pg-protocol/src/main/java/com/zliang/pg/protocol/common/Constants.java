package com.zliang.pg.protocol.common;

/**
 * @author 赵亮
 * @version 1.0
 * @description: TODO
 * @date 2024/12/23 17:16
 */
public interface Constants {
    // The value (major version) is chosen to contain 1234 in the most significant 16 bits, this code must not be the same as any
    short VERSION_MAJOR_SPECIAL = 1234;
    // The value (minor version) which is used to identify SslRequest
    short VERSION_MINOR_CANCEL = 5678;
    // The value (minor version) which is used to identify CancelRequest
    short VERSION_MINOR_SSL = 5679;
    // The value (minor version) which is used to identify GSSENCRequest
    short VERSION_MINOR_GSSENC = 5680;
}
