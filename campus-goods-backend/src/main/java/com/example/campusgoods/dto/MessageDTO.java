package com.example.campusgoods.dto;

import lombok.Data;

/**
 * 发送留言 / 回复参数
 */
@Data
public class MessageDTO {

    /** 商品ID */
    private Long goodsId;

    /** 留言内容 */
    private String content;

    /**
     * 接收人ID（选填）
     * 为空时默认发给商品发布人；卖家回复买家时由前端传入买家ID
     */
    private Long toUserId;
}
