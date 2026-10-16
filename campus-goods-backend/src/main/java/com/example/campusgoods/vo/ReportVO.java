package com.example.campusgoods.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 举报视图对象
 */
@Data
public class ReportVO {

    /** 举报ID */
    private Long id;

    /** 被举报商品ID */
    private Long goodsId;

    /** 被举报商品标题 */
    private String goodsTitle;

    /** 举报人用户ID */
    private Long userId;

    /** 举报人昵称 */
    private String userName;

    /** 举报原因 */
    private String reason;

    /** 状态：0待处理 1已处理 */
    private Integer status;

    /** 举报时间 */
    private LocalDateTime createTime;
}
