package com.example.campusgoods.dto;

import lombok.Data;

/**
 * 登录参数
 */
@Data
public class LoginDTO {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;
}
