package com.example.campusgoods.config;

import com.example.campusgoods.vo.Result;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理：业务异常（RuntimeException）统一转为 Result 返回，
 * 避免向前端暴露默认错误页
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常：msg 为具体中文提示 */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntime(RuntimeException e) {
        return Result.error(e.getMessage() == null ? "操作失败" : e.getMessage());
    }

    /** 请求参数类型不匹配（如 page=abc）：返回友好提示，不暴露异常堆栈 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<?> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return Result.error("参数 " + e.getName() + " 格式不正确");
    }

    /** 请求体缺失或 JSON 格式错误 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleNotReadable(HttpMessageNotReadableException e) {
        return Result.error("请求参数格式不正确");
    }

    /** 其他未捕获异常 */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        return Result.error("服务器异常：" + e.getMessage());
    }
}
