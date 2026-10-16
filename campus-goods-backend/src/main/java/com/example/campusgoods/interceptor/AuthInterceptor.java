package com.example.campusgoods.interceptor;

import com.example.campusgoods.utils.JwtUtils;
import com.example.campusgoods.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录鉴权拦截器：拦截 /api/**
 * 白名单请求直接放行（但仍尝试解析 token，供商品详情 favorite 字段等使用）
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 无论是否白名单，都尝试解析 token；解析出的 userId、role 放入 request attribute
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        JwtUtils.Payload payload = JwtUtils.parse(token);
        if (payload != null) {
            request.setAttribute("userId", payload.getUid());
            request.setAttribute("role", payload.getRole());
        }

        // 白名单放行：登录、注册、商品列表、分类列表、商品详情
        if (isWhitelist(request.getMethod(), request.getRequestURI())) {
            UserContext.setCurrentUserId(payload == null ? null : payload.getUid());
            return true;
        }

        // 其余请求必须携带有效 token
        if (payload == null) {
            writeJson(response, 401, "请先登录");
            return false;
        }

        // 管理员接口仅 role=0 可访问
        if (request.getRequestURI().startsWith("/api/admin")) {
            Integer role = payload.getRole();
            if (role == null || role != 0) {
                writeJson(response, 403, "无权限访问");
                return false;
            }
        }
        // 校验通过后写入线程上下文，供 Service 层记录操作日志使用
        UserContext.setCurrentUserId(payload.getUid());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 线程会被复用，请求结束必须清理，否则下一个请求可能读到上一个操作人
        UserContext.clear();
    }

    /**
     * 白名单：POST /api/auth/login、POST /api/auth/register、
     * GET /api/goods/list、GET /api/goods/categories、GET /api/goods/{纯数字}、
     * GET /api/notice/list、GET /api/message/goods/{纯数字}
     */
    private boolean isWhitelist(String method, String uri) {
        if ("POST".equals(method) && "/api/auth/login".equals(uri)) {
            return true;
        }
        if ("POST".equals(method) && "/api/auth/register".equals(uri)) {
            return true;
        }
        if ("GET".equals(method)) {
            if ("/api/goods/list".equals(uri)) {
                return true;
            }
            if ("/api/goods/categories".equals(uri)) {
                return true;
            }
            if ("/api/notice/list".equals(uri)) {
                return true;
            }
            if (uri.matches("^/api/goods/\\d+$")) {
                return true;
            }
            if (uri.matches("^/api/message/goods/\\d+$")) {
                return true;
            }
        }
        return false;
    }

    /** 直接向响应写统一 JSON（HTTP 状态码保持 200，由 code 区分） */
    private void writeJson(HttpServletResponse response, int code, String msg) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("msg", msg);
        result.put("data", null);
        PrintWriter writer = response.getWriter();
        writer.write(MAPPER.writeValueAsString(result));
        writer.flush();
    }
}
