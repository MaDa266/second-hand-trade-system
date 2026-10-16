package com.example.campusgoods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 留言实体
 */
@Data
@TableName("message")
public class Message {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商品ID */
    private Long goodsId;

    /** 留言人用户ID */
    private Long fromUserId;

    /** 接收人用户ID */
    private Long toUserId;

    /** 留言内容 */
    private String content;

    /** 是否已读：0未读 1已读 */
    private Integer isRead;

    /** 留言时间 */
    private LocalDateTime createTime;
}
