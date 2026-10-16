package com.example.campusgoods.dto;

import lombok.Data;

/**
 * 修改日志备注参数
 * 只允许修改备注，操作人、动作、时间、详情属于审计证据不可更改
 */
@Data
public class LogRemarkDTO {

    /** 管理员备注 */
    private String remark;
}
