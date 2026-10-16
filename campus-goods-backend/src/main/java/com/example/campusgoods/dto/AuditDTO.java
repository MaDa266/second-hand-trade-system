package com.example.campusgoods.dto;

import lombok.Data;

/**
 * 商品审核参数：status=1 审核通过，status=4 审核不通过
 */
@Data
public class AuditDTO {

    /** 目标状态：1在售 4审核不通过 */
    private Integer status;
}
