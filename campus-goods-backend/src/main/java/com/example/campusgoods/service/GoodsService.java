package com.example.campusgoods.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campusgoods.entity.Category;
import com.example.campusgoods.entity.Favorite;
import com.example.campusgoods.entity.Goods;
import com.example.campusgoods.entity.Message;
import com.example.campusgoods.entity.User;
import com.example.campusgoods.mapper.CategoryMapper;
import com.example.campusgoods.mapper.FavoriteMapper;
import com.example.campusgoods.mapper.GoodsMapper;
import com.example.campusgoods.mapper.MessageMapper;
import com.example.campusgoods.mapper.UserMapper;
import com.example.campusgoods.utils.Validators;
import com.example.campusgoods.vo.GoodsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品业务
 */
@Service
public class GoodsService extends ServiceImpl<GoodsMapper, Goods> {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private OperationLogService operationLogService;

    /**
     * 在售商品分页（首页列表）：仅 status=1，标题模糊搜索，创建时间倒序
     */
    public Page<GoodsVO> pageOnSale(String keyword, Long categoryId, Integer page, Integer size) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getStatus, 1);
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Goods::getTitle, keyword.trim());
        }
        if (categoryId != null) {
            wrapper.eq(Goods::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Goods::getCreateTime);
        return toVOPage(this.page(new Page<>(Validators.page(page), Validators.size(size, 12)), wrapper));
    }

    /**
     * 我的发布分页：当前用户全部状态商品，创建时间倒序
     */
    public Page<GoodsVO> pageMy(Long userId, Integer page, Integer size) {
        Validators.notNull(userId, "登录状态");
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getUserId, userId).orderByDesc(Goods::getCreateTime);
        return toVOPage(this.page(new Page<>(Validators.page(page), Validators.size(size, 10)), wrapper));
    }

    /**
     * 管理员商品分页：status 为空时查全部，创建时间倒序
     */
    public Page<GoodsVO> pageAudit(Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Goods::getStatus, status);
        }
        wrapper.orderByDesc(Goods::getCreateTime);
        return toVOPage(this.page(new Page<>(Validators.page(page), Validators.size(size, 10)), wrapper));
    }

    /**
     * 商品详情：浏览量 +1，附带卖家昵称、分类名、当前用户收藏状态
     */
    public GoodsVO detail(Long id, Long viewerId) {
        Validators.notNull(id, "商品");
        Goods goods = this.getById(id);
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }
        // 浏览量原子自增
        this.update(new UpdateWrapper<Goods>().eq("id", id)
                .setSql("view_count = IFNULL(view_count, 0) + 1"));
        goods.setViewCount(goods.getViewCount() == null ? 1 : goods.getViewCount() + 1);

        GoodsVO vo = toVO(goods);
        if (viewerId != null) {
            Long count = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                    .eq(Favorite::getUserId, viewerId)
                    .eq(Favorite::getGoodsId, id));
            vo.setFavorite(count != null && count > 0);
        } else {
            vo.setFavorite(false);
        }
        return vo;
    }

    /**
     * 发布商品：发布人为当前登录人，状态待审核
     */
    public void publish(Goods goods, Long userId) {
        Validators.notNull(userId, "登录状态");
        normalizeAndCheck(goods);

        goods.setId(null);
        goods.setUserId(userId);
        goods.setStatus(0);
        goods.setViewCount(0);
        LocalDateTime now = LocalDateTime.now();
        goods.setCreateTime(now);
        goods.setUpdateTime(now);
        this.save(goods);
        operationLogService.record(OperationLogService.MODULE_GOODS, "发布商品", "商品", goods.getId(),
                "标题：" + goods.getTitle() + "，价格：" + goods.getPrice() + "，提交审核");
    }

    /**
     * 编辑商品：仅发布人本人可改；在售 / 审核不通过的商品修改后重新进入待审核
     */
    public void updateGoods(Long id, Goods goods, Long userId) {
        Validators.notNull(userId, "登录状态");
        Goods db = this.getById(id);
        if (db == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!db.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        normalizeAndCheck(goods);

        goods.setId(id);
        goods.setUserId(db.getUserId());
        goods.setStatus(nextStatusAfterEdit(db.getStatus()));
        goods.setUpdateTime(LocalDateTime.now());
        this.updateById(goods);
        String statusTip = !goods.getStatus().equals(db.getStatus()) ? "，重新进入待审核" : "";
        operationLogService.record(OperationLogService.MODULE_GOODS, "编辑商品", "商品", id,
                "标题：" + goods.getTitle() + "，价格：" + goods.getPrice() + statusTip);
    }

    /**
     * 删除商品：仅发布人本人，同时删除该商品的收藏与留言记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteGoods(Long id, Long userId) {
        Validators.notNull(userId, "登录状态");
        Goods db = this.getById(id);
        if (db == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!db.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        this.removeById(id);
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>().eq(Favorite::getGoodsId, id));
        messageMapper.delete(new LambdaQueryWrapper<Message>().eq(Message::getGoodsId, id));
        operationLogService.record(OperationLogService.MODULE_GOODS, "删除商品", "商品", id,
                "标题：" + db.getTitle() + "，同时清理了收藏与留言");
    }

    /**
     * 标记已售：仅发布人本人，状态置 2
     */
    public void markSold(Long id, Long userId) {
        Validators.notNull(userId, "登录状态");
        Goods db = this.getById(id);
        if (db == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!db.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        db.setStatus(2);
        db.setUpdateTime(LocalDateTime.now());
        this.updateById(db);
        operationLogService.record(OperationLogService.MODULE_GOODS, "标记已售", "商品", id,
                "标题：" + db.getTitle());
    }

    /**
     * 管理员审核商品：status=1 通过 / 4 不通过
     */
    public void audit(Long id, Integer status) {
        Goods db = this.getById(id);
        if (db == null) {
            throw new RuntimeException("商品不存在");
        }
        db.setStatus(status);
        db.setUpdateTime(LocalDateTime.now());
        this.updateById(db);
        operationLogService.record(OperationLogService.MODULE_GOODS,
                status != null && status == 1 ? "审核通过" : "审核不通过", "商品", id,
                "标题：" + db.getTitle() + "，卖家：" + db.getUserId());
    }

    /**
     * 管理员下架商品：状态置 3
     */
    public void offline(Long id) {
        Goods db = this.getById(id);
        if (db == null) {
            throw new RuntimeException("商品不存在");
        }
        db.setStatus(3);
        db.setUpdateTime(LocalDateTime.now());
        this.updateById(db);
        operationLogService.record(OperationLogService.MODULE_GOODS, "下架商品", "商品", id,
                "标题：" + db.getTitle());
    }

    /**
     * 单个商品转 VO（附带卖家昵称、分类名）
     */
    public GoodsVO toVO(Goods goods) {
        GoodsVO vo = new GoodsVO();
        BeanUtils.copyProperties(goods, vo);
        if (goods.getUserId() != null) {
            User seller = userMapper.selectById(goods.getUserId());
            if (seller != null) {
                vo.setSellerName(displayName(seller.getNickname(), seller.getUsername()));
            }
        }
        if (goods.getCategoryId() != null) {
            Category category = categoryMapper.selectById(goods.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        return vo;
    }

    /**
     * 商品列表批量转 VO：一次查出全部卖家与分类，避免 N+1 查询
     */
    public List<GoodsVO> toVOList(List<Goods> goodsList) {
        List<GoodsVO> voList = new ArrayList<>();
        if (goodsList == null || goodsList.isEmpty()) {
            return voList;
        }
        Set<Long> userIds = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();
        for (Goods g : goodsList) {
            if (g.getUserId() != null) {
                userIds.add(g.getUserId());
            }
            if (g.getCategoryId() != null) {
                categoryIds.add(g.getCategoryId());
            }
        }
        Map<Long, User> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        Map<Long, Category> categoryMap = categoryIds.isEmpty() ? Collections.emptyMap()
                : categoryMapper.selectBatchIds(categoryIds).stream()
                    .collect(Collectors.toMap(Category::getId, c -> c));
        for (Goods g : goodsList) {
            GoodsVO vo = new GoodsVO();
            BeanUtils.copyProperties(g, vo);
            User seller = userMap.get(g.getUserId());
            if (seller != null) {
                vo.setSellerName(displayName(seller.getNickname(), seller.getUsername()));
            }
            Category category = categoryMap.get(g.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 商品分页转 VO 分页
     */
    public Page<GoodsVO> toVOPage(Page<Goods> goodsPage) {
        Page<GoodsVO> voPage = new Page<>(goodsPage.getCurrent(), goodsPage.getSize(), goodsPage.getTotal());
        voPage.setRecords(toVOList(goodsPage.getRecords()));
        return voPage;
    }

    /**
     * 去空格、校验长度与金额区间，并确认分类存在且启用
     */
    private void normalizeAndCheck(Goods goods) {
        goods.setTitle(Validators.requiredText(goods.getTitle(), "商品标题", 100));
        goods.setPrice(Validators.requiredPrice(goods.getPrice(), "价格"));
        goods.setOldPrice(Validators.optionalPrice(goods.getOldPrice(), "原价"));
        goods.setDescription(Validators.optionalText(goods.getDescription(), "商品描述", 2000));
        goods.setConditionLevel(Validators.optionalText(goods.getConditionLevel(), "成色", 20));
        goods.setTradePlace(Validators.optionalText(goods.getTradePlace(), "交易地点", 100));
        goods.setCover(Validators.optionalText(goods.getCover(), "封面图", 255));
        goods.setImages(Validators.optionalText(goods.getImages(), "商品图片", 2000));

        Validators.notNull(goods.getCategoryId(), "商品分类");
        Category category = categoryMapper.selectById(goods.getCategoryId());
        if (category == null) {
            throw new RuntimeException("商品分类不存在");
        }
        if (category.getStatus() != null && category.getStatus() == 0) {
            throw new RuntimeException("该分类已停用，请选择其他分类");
        }
    }

    /**
     * 编辑后的商品状态：在售(1) 与审核不通过(4) 的商品被修改后重新进入待审核(0)；
     * 待审核保持，已售(2) 与已下架(3) 不变，避免编辑绕过审核或绕过管理员下架
     */
    private Integer nextStatusAfterEdit(Integer current) {
        if (current != null && (current == 1 || current == 4)) {
            return 0;
        }
        return current;
    }

    /** 昵称为空时回退显示用户名 */
    private String displayName(String nickname, String username) {
        return (nickname != null && !nickname.isEmpty()) ? nickname : username;
    }
}
