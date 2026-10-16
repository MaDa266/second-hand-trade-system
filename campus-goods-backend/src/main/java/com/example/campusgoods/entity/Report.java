package com.example.campusgoods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 举报实体
 */
@Data
@TableName("report")
public class Report {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 被举报商品ID */
    private Long goodsId;

    /** 举报人用户ID */
    private Long userId;

    /** 举报原因 */
    private String reason;

    /** 状态：0待处理 1已处理 */
    private Integer status;

    /** 举报时间 */
    private LocalDateTime createTime;
}
