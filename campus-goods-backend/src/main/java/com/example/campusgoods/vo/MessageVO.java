package com.example.campusgoods.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 留言视图对象
 */
@Data
public class MessageVO {

    /** 留言ID */
    private Long id;

    /** 商品ID */
    private Long goodsId;

    /** 商品标题 */
    private String goodsTitle;

    /** 留言人用户ID */
    private Long fromUserId;

    /** 留言人昵称 */
    private String fromName;

    /** 接收人用户ID */
    private Long toUserId;

    /** 接收人昵称 */
    private String toName;

    /** 留言内容 */
    private String content;

    /** 是否已读：0未读 1已读 */
    private Integer isRead;

    /** 留言时间 */
    private LocalDateTime createTime;
}
