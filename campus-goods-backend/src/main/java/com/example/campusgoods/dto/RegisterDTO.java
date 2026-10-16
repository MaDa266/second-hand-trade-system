package com.example.campusgoods.dto;

import lombok.Data;

/**
 * 注册参数
 */
@Data
public class RegisterDTO {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 手机号 */
    private String phone;
}
