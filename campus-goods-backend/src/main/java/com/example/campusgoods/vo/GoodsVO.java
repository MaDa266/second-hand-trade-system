package com.example.campusgoods.vo;

import com.example.campusgoods.entity.Goods;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商品视图对象：在商品实体基础上附加卖家昵称、分类名、收藏状态与收藏时间
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsVO extends Goods {

    /** 卖家昵称 */
    private String sellerName;

    /** 分类名称 */
    private String categoryName;

    /** 当前用户是否已收藏（未登录时为 false） */
    private Boolean favorite;

    /** 收藏时间（我的收藏列表使用） */
    private LocalDateTime favoriteTime;
}
