package com.zjnu.demo.common;

/**
 * 业务异常：用户名重复、资源不存在等可预期的失败
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
