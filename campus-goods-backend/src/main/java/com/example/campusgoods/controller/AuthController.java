package com.example.campusgoods.controller;

import com.example.campusgoods.dto.LoginDTO;
import com.example.campusgoods.dto.RegisterDTO;
import com.example.campusgoods.entity.User;
import com.example.campusgoods.service.UserService;
import com.example.campusgoods.utils.JwtUtils;
import com.example.campusgoods.utils.UserContext;
import com.example.campusgoods.vo.LoginVO;
import com.example.campusgoods.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 认证接口：注册、登录、退出、当前用户
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /** 学生注册 */
    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success(null);
    }

    /** 登录：返回 token 与用户信息（不含密码） */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        User user = userService.login(dto);
        String token = JwtUtils.create(user.getId(), user.getRole());
        user.setPassword(null);
        return Result.success(new LoginVO(token, user));
    }

    /** 退出登录（前端清除 token 即可，接口仅作约定） */
    @PostMapping("/logout")
    public Result<?> logout() {
        return Result.success(null);
    }

    /** 获取当前登录用户 */
    @GetMapping("/me")
    public Result<User> me(HttpServletRequest request) {
        Long userId = UserContext.getUserId(request);
        User user = userService.getByIdWithoutPassword(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    /** 修改个人资料（昵称、手机号、头像） */
    @PutMapping("/me")
    public Result<User> updateMe(HttpServletRequest request, @RequestBody User body) {
        Long userId = UserContext.getUserId(request);
        return Result.success(userService.updateProfile(userId, body));
    }
}
