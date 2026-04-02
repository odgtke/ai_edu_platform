package com.chinaunicom.edu.common.handler;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.common.exception.BaseException;
import com.chinaunicom.edu.common.exception.BusinessException;
import com.chinaunicom.edu.common.enums.ExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.chinaunicom.edu")
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Object> handleBusinessException(BusinessException e) {
        log.error("业务异常: ", e);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理自定义基础异常
     */
    @ExceptionHandler(BaseException.class)
    public Result<Object> handleBaseException(BaseException e) {
        log.error("基础异常: ", e);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常 (Validator)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数验证异常: ", e);
        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.validateFailed(message);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Object> handleBindException(BindException e) {
        log.error("参数绑定异常: ", e);
        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.validateFailed(message);
    }

    /**
     * 处理参数约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Object> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("参数约束违反异常: ", e);
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return Result.validateFailed(message);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<Object> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常: ", e);
        return Result.error(ExceptionEnum.SYSTEM_ERROR.getCode(), "空指针异常");
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Object> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: ", e);
        return Result.error(ExceptionEnum.SYSTEM_ERROR.getCode(), e.getMessage());
    }

    /**
     * 处理一般异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error(ExceptionEnum.SYSTEM_ERROR.getCode(), "系统繁忙，请稍后再试");
    }
}