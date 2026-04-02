package com.chinaunicom.edu.exception;

/**
 * 业务异常类
 * 用于处理业务逻辑错误
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public Integer getCode() {
        return code;
    }
}
