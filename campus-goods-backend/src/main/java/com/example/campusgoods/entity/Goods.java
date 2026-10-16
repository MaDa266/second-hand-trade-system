package com.example.campusgoods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@TableName("goods")
public class Goods {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发布人用户ID */
    private Long userId;

    /** 分类ID */
    private Long categoryId;

    /** 标题 */
    private String title;

    /** 描述 */
    private String description;

    /** 价格 */
    private BigDecimal price;

    /** 原价 */
    private BigDecimal oldPrice;

    /** 封面图（相对路径 /uploads/xxx） */
    private String cover;

    /** 图片路径，多张逗号分隔（相对路径） */
    private String images;

    /** 成色 */
    private String conditionLevel;

    /** 交易地点 */
    private String tradePlace;

    /** 状态：0待审核 1在售 2已售 3下架 4审核不通过 */
    private Integer status;

    /** 浏览量 */
    private Integer viewCount;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
