package com.example.campusgoods.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campusgoods.dto.LoginDTO;
import com.example.campusgoods.dto.RegisterDTO;
import com.example.campusgoods.entity.User;
import com.example.campusgoods.mapper.UserMapper;
import com.example.campusgoods.utils.Validators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户业务
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    /** 用户名最大长度，与建表语句 username VARCHAR(50) 一致 */
    private static final int USERNAME_MAX = 50;

    /** 密码长度区间 */
    private static final int PASSWORD_MIN = 6;

    private static final int PASSWORD_MAX = 20;

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 注册：用户名重复报错，默认角色学生（role=1）、状态正常（status=1）
     */
    public void register(RegisterDTO dto) {
        String username = Validators.requiredText(dto.getUsername(), "用户名", USERNAME_MAX);
        String password = Validators.requiredText(dto.getPassword(), "密码", PASSWORD_MAX);
        if (password.length() < PASSWORD_MIN) {
            throw new RuntimeException("密码长度不能少于 " + PASSWORD_MIN + " 位");
        }
        String nickname = Validators.optionalText(dto.getNickname(), "昵称", 50);
        String phone = Validators.optionalPhone(dto.getPhone());

        Long count = this.lambdaQuery().eq(User::getUsername, username).count();
        if (count != null && count > 0) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        // 昵称未填时默认使用用户名
        user.setNickname(nickname == null || nickname.isEmpty() ? username : nickname);
        user.setPhone(phone);
        user.setRole(1);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        try {
            this.save(user);
        } catch (DuplicateKeyException e) {
            // 并发注册同一用户名时由唯一索引兜底
            throw new RuntimeException("用户名已存在");
        }
        // 注册时还没有登录态，显式指定操作人
        operationLogService.recordAs(user.getId(), user.getUsername(),
                OperationLogService.MODULE_AUTH, "用户注册", "用户", user.getId(),
                "新用户注册：" + user.getUsername());
    }

    /**
     * 登录校验：用户名或密码错误、账号被禁用时报错
     */
    public User login(LoginDTO dto) {
        String username = Validators.requiredText(dto.getUsername(), "用户名", USERNAME_MAX);
        String password = Validators.requiredText(dto.getPassword(), "密码", PASSWORD_MAX);

        User user = this.lambdaQuery().eq(User::getUsername, username).one();
        if (user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }
        // 登录发生在鉴权之前，显式指定操作人
        operationLogService.recordAs(user.getId(), user.getUsername(),
                OperationLogService.MODULE_AUTH, "用户登录", "用户", user.getId(),
                "登录成功" + (user.getRole() != null && user.getRole() == 0 ? "（管理员）" : ""));
        return user;
    }

    /**
     * 更新个人资料（仅昵称、手机号、头像），返回最新用户信息（不含密码）
     */
    public User updateProfile(Long userId, User body) {
        Validators.notNull(userId, "登录状态");
        User user = new User();
        user.setId(userId);
        user.setNickname(Validators.optionalText(body.getNickname(), "昵称", 50));
        user.setPhone(Validators.optionalPhone(body.getPhone()));
        user.setAvatar(Validators.optionalText(body.getAvatar(), "头像", 255));
        this.updateById(user);
        operationLogService.record(OperationLogService.MODULE_AUTH, "修改个人资料", "用户", userId,
                "昵称：" + user.getNickname() + "，手机号：" + (user.getPhone() == null ? "未填" : user.getPhone()));
        return getByIdWithoutPassword(userId);
    }

    /**
     * 按ID查询用户并抹去密码
     */
    public User getByIdWithoutPassword(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            return null;
        }
        user.setPassword(null);
        return user;
    }
}
