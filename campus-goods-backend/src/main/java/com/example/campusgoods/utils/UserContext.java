package com.example.campusgoods.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * 当前登录用户上下文
 * 1. 按请求取值：拦截器把 userId / role 放进 request attribute，Controller 层使用；
 * 2. ThreadLocal 取值：Service 层拿不到 request，通过线程变量读取当前操作人（用于写操作日志）。
 */
public class UserContext {

    /** 当前请求的操作人ID，由 AuthInterceptor 校验通过后写入 */
    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();

    /** 当前请求的操作人用户名，首次写日志时解析并缓存 */
    private static final ThreadLocal<String> CURRENT_USERNAME = new ThreadLocal<>();

    private UserContext() {
    }

    /** 获取当前登录用户ID（白名单接口未携带有效 token 时为 null） */
    public static Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    /** 获取当前登录用户角色 */
    public static Integer getRole(HttpServletRequest request) {
        return (Integer) request.getAttribute("role");
    }

    /** 获取当前请求的操作人ID（Service 层使用，未登录时为 null） */
    public static Long getCurrentUserId() {
        return CURRENT_USER_ID.get();
    }

    /** 获取当前请求的操作人用户名（可能为 null，由日志服务解析后缓存） */
    public static String getCurrentUsername() {
        return CURRENT_USERNAME.get();
    }

    /** 缓存当前请求的操作人用户名，避免同一次请求内重复查库 */
    public static void setCurrentUsername(String username) {
        CURRENT_USERNAME.set(username);
    }

    /** 由拦截器在校验通过后写入 */
    public static void setCurrentUserId(Long userId) {
        CURRENT_USER_ID.set(userId);
    }

    /** 请求结束后必须清理，避免线程复用造成串号 */
    public static void clear() {
        CURRENT_USER_ID.remove();
        CURRENT_USERNAME.remove();
    }
}
