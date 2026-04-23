package com.sloth.boot.common.annotation;

/**
 * 脱敏类型
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public enum DesensitizeType {

    /**
     * 手机号
     */
    MOBILE,

    /**
     * 身份证号
     */
    ID_CARD,

    /**
     * 邮箱
     */
    EMAIL,

    /**
     * 银行卡号
     */
    BANK_CARD,

    /**
     * 中文名称
     */
    NAME,

    /**
     * 地址
     */
    ADDRESS,

    /**
     * 自定义
     */
    CUSTOM
}
