package com.sloth.boot.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sloth.boot.common.constant.CommonConstant;
import com.sloth.boot.common.context.TraceContext;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 请求追踪ID
     */
    private String traceId;

    /**
     * 响应时间戳
     */
    private long timestamp;

    /**
     * 私有构造函数
     */
    private R() {
        this.timestamp = System.currentTimeMillis();
        this.traceId = TraceContext.getTraceId();
    }

    /**
     * 成功响应
     *
     * @param <T> 数据类型
     * @return 统一响应
     */
    public static <T> R<T> ok() {
        R<T> r = new R<>();
        r.setCode(CommonConstant.SUCCESS);
        r.setMsg("操作成功");
        return r;
    }

    /**
     * 成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 统一响应
     */
    public static <T> R<T> ok(T data) {
        R<T> r = ok();
        r.setData(data);
        return r;
    }

    /**
     * 成功响应
     *
     * @param msg 响应消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 统一响应
     */
    public static <T> R<T> ok(String msg, T data) {
        R<T> r = ok();
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    /**
     * 失败响应
     *
     * @param <T> 数据类型
     * @return 统一响应
     */
    public static <T> R<T> fail() {
        R<T> r = new R<>();
        r.setCode(CommonConstant.FAIL);
        r.setMsg("操作失败");
        return r;
    }

    /**
     * 失败响应
     *
     * @param msg 响应消息
     * @param <T> 数据类型
     * @return 统一响应
     */
    public static <T> R<T> fail(String msg) {
        R<T> r = fail();
        r.setMsg(msg);
        return r;
    }

    /**
     * 失败响应
     *
     * @param code 响应码
     * @param msg  响应消息
     * @param <T>  数据类型
     * @return 统一响应
     */
    public static <T> R<T> fail(int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    /**
     * 失败响应
     *
     * @param errorCode 错误码
     * @param <T>       数据类型
     * @return 统一响应
     */
    public static <T> R<T> fail(ErrorCode errorCode) {
        R<T> r = new R<>();
        r.setCode(errorCode.getCode());
        r.setMsg(errorCode.getMsg());
        return r;
    }

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return CommonConstant.SUCCESS == this.code;
    }
}
