package com.example.campusgoods.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campusgoods.entity.Category;
import com.example.campusgoods.entity.Goods;
import com.example.campusgoods.service.CategoryService;
import com.example.campusgoods.service.GoodsService;
import com.example.campusgoods.utils.UserContext;
import com.example.campusgoods.vo.GoodsVO;
import com.example.campusgoods.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 商品接口：列表、详情、发布、编辑、删除、标记已售、我的发布
 */
@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private CategoryService categoryService;

    /** 在售商品分页列表（公开） */
    @GetMapping("/list")
    public Result<Page<GoodsVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size) {
        return Result.success(goodsService.pageOnSale(keyword, categoryId, page, size));
    }

    /** 启用分类列表（公开，首页分类筛选用） */
    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        return Result.success(categoryService.listEnabled());
    }

    /** 商品详情（公开）：浏览量+1，附带收藏状态 */
    @GetMapping("/{id}")
    public Result<GoodsVO> detail(@PathVariable Long id, HttpServletRequest request) {
        return Result.success(goodsService.detail(id, UserContext.getUserId(request)));
    }

    /** 发布商品：提交后待管理员审核 */
    @PostMapping
    public Result<?> publish(@RequestBody Goods goods, HttpServletRequest request) {
        goodsService.publish(goods, UserContext.getUserId(request));
        return Result.success(null);
    }

    /** 编辑商品：仅发布人本人 */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Goods goods, HttpServletRequest request) {
        goodsService.updateGoods(id, goods, UserContext.getUserId(request));
        return Result.success(null);
    }

    /** 删除商品：仅发布人本人 */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        goodsService.deleteGoods(id, UserContext.getUserId(request));
        return Result.success(null);
    }

    /** 标记已售：仅发布人本人 */
    @PutMapping("/{id}/sold")
    public Result<?> sold(@PathVariable Long id, HttpServletRequest request) {
        goodsService.markSold(id, UserContext.getUserId(request));
        return Result.success(null);
    }

    /** 我的发布分页 */
    @GetMapping("/my")
    public Result<Page<GoodsVO>> my(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        return Result.success(goodsService.pageMy(UserContext.getUserId(request), page, size));
    }
}
