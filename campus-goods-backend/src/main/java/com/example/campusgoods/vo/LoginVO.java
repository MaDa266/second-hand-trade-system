package com.example.campusgoods.vo;

import com.example.campusgoods.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录返回对象：token + 用户信息（不含密码）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    /** JWT token */
    private String token;

    /** 用户信息 */
    private User user;
}
