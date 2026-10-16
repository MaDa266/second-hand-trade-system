package com.example.campusgoods.vo;

import lombok.Data;

/**
 * 统一返回结果：{"code":200,"msg":"操作成功","data":...}
 */
@Data
public class Result<T> {

    /** 状态码：200成功 500失败 401未登录 403无权限 */
    private Integer code;

    /** 提示信息 */
    private String msg;

    /** 数据 */
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }
}
