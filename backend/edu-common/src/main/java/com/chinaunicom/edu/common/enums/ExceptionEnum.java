package com.chinaunicom.edu.common.enums;

/**
 * 异常状态码枚举
 */
public enum ExceptionEnum {
    // 通用异常 10000-10099
    SYSTEM_ERROR(10000, "系统内部错误"),
    REQUEST_PARAMS_ERROR(10001, "请求参数错误"),
    RESOURCE_NOT_FOUND(10002, "资源不存在"),
    DATA_INTEGRITY_VIOLATION(10003, "数据完整性违规"),
    DUPLICATE_RESOURCE(10004, "重复资源"),
    INVALID_OPERATION(10005, "无效操作"),

    // 用户相关异常 10100-10199
    USER_LOGIN_ERROR(10100, "用户登录失败"),
    USER_NOT_EXIST(10101, "用户不存在"),
    USER_ACCOUNT_LOCKED(10102, "账户已被锁定"),
    USER_ACCOUNT_DISABLED(10103, "账户已被禁用"),
    USER_PASSWORD_ERROR(10104, "用户密码错误"),
    USER_NOT_LOGGED_IN(10105, "用户未登录"),
    USER_TOKEN_EXPIRED(10106, "用户令牌已过期"),
    USER_NO_PERMISSION(10107, "用户无权限操作"),

    // 课程相关异常 10200-10299
    COURSE_NOT_EXIST(10200, "课程不存在"),
    COURSE_ALREADY_EXISTS(10201, "课程已存在"),
    COURSE_PUBLISH_ERROR(10202, "课程发布失败"),

    // 学习相关异常 10300-10399
    LEARNING_RECORD_NOT_FOUND(10300, "学习记录不存在"),
    ASSIGNMENT_SUBMISSION_ERROR(10301, "作业提交失败"),
    ASSESSMENT_ERROR(10302, "测评失败");

    private final Integer code;
    private final String message;

    ExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return  this.code;
    }

    public String getMessage() {
        return  this.message;
    }
}