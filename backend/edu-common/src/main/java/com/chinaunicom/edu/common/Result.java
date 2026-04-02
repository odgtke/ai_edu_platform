package com.chinaunicom.edu.common;

import lombok.Data;
import java.io.Serializable;

/**
 * 统一API响应结果封装
 * @param <T> 数据类型
 */
@Data
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 响应码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public Result(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }
    
    public Result(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }
    
    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功");
    }
    
    /**
     * 成功响应带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }
    
    /**
     * 成功响应带消息和数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    
    /**
     * 失败响应
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败");
    }
    
    /**
     * 失败响应带消息
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message);
    }
    
    /**
     * 失败响应带状态码和消息
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }
    
    /**
     * 参数校验失败
     */
    public static <T> Result<T> validateFailed() {
        return new Result<>(400, "参数校验失败");
    }
    
    /**
     * 参数校验失败带消息
     */
    public static <T> Result<T> validateFailed(String message) {
        return new Result<>(400, message);
    }
    
    /**
     * 未认证返回结果
     */
    public static <T> Result<T> unauthorized(T data) {
        return new Result<>(401, "暂未登录或token已经过期", data);
    }
    
    /**
     * 未授权返回结果
     */
    public static <T> Result<T> forbidden(T data) {
        return new Result<>(403, "没有相关权限", data);
    }
}
