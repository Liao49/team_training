package com.zjnu.demo.common;

/**
 * 统一响应包装：code=0 成功，code=1 业务失败
 */
public record Result<T>(int code, String message, T data) {

    public static <T> Result<T> ok(T data)        { return new Result<>(0, "success", data); }

    public static <T> Result<T> error(String msg) { return new Result<>(1, msg, null); }
}
