package com.example.campusgoods.dto;

import lombok.Data;

/**
 * 提交举报参数
 */
@Data
public class ReportDTO {

    /** 被举报商品ID */
    private Long goodsId;

    /** 举报原因 */
    private String reason;
}
