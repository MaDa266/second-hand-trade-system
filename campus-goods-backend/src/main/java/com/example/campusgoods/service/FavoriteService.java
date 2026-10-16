package com.example.campusgoods.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campusgoods.entity.Favorite;
import com.example.campusgoods.entity.Goods;
import com.example.campusgoods.mapper.FavoriteMapper;
import com.example.campusgoods.utils.Validators;
import com.example.campusgoods.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收藏业务
 */
@Service
public class FavoriteService extends ServiceImpl<FavoriteMapper, Favorite> {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OperationLogService operationLogService;

    /**
     * 收藏商品：重复收藏幂等不报错（由 uk_user_goods 唯一索引兜底并发）
     */
    public void add(Long userId, Long goodsId) {
        Validators.notNull(userId, "登录状态");
        Validators.notNull(goodsId, "商品");
        Goods goods = goodsService.getById(goodsId);
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }
        if (userId.equals(goods.getUserId())) {
            throw new RuntimeException("不能收藏自己发布的商品");
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setGoodsId(goodsId);
        favorite.setCreateTime(LocalDateTime.now());
        try {
            this.save(favorite);
        } catch (DuplicateKeyException e) {
            // 并发下的重复收藏，唯一索引已保证只有一条，按幂等处理
            return;
        }
        operationLogService.record(OperationLogService.MODULE_FAVORITE, "收藏商品", "商品", goodsId,
                "标题：" + goods.getTitle());
    }

    /**
     * 取消收藏
     */
    public void remove(Long userId, Long goodsId) {
        Validators.notNull(userId, "登录状态");
        Validators.notNull(goodsId, "商品");
        boolean removed = this.remove(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getGoodsId, goodsId));
        if (removed) {
            operationLogService.record(OperationLogService.MODULE_FAVORITE, "取消收藏", "商品", goodsId,
                    "取消收藏商品#" + goodsId);
        }
    }

    /**
     * 我的收藏列表：按收藏时间倒序，附带卖家昵称与分类名。
     * 先按收藏顺序整理商品，再一次性补齐关联信息，避免逐条查库
     */
    public List<GoodsVO> listMy(Long userId) {
        Validators.notNull(userId, "登录状态");
        List<Favorite> favorites = this.lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime)
                .list();
        if (favorites.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> goodsIds = favorites.stream().map(Favorite::getGoodsId).collect(Collectors.toList());
        Map<Long, Goods> goodsMap = goodsService.listByIds(goodsIds).stream()
                .collect(Collectors.toMap(Goods::getId, g -> g));

        List<Goods> ordered = new ArrayList<>();
        Map<Long, LocalDateTime> favoriteTimeMap = new HashMap<>();
        for (Favorite f : favorites) {
            Goods goods = goodsMap.get(f.getGoodsId());
            if (goods != null) {
                ordered.add(goods);
                favoriteTimeMap.put(goods.getId(), f.getCreateTime());
            }
        }
        List<GoodsVO> result = goodsService.toVOList(ordered);
        for (GoodsVO vo : result) {
            vo.setFavorite(true);
            vo.setFavoriteTime(favoriteTimeMap.get(vo.getId()));
        }
        return result;
    }
}
