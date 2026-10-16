package com.example.campusgoods.controller;

import com.example.campusgoods.service.FavoriteService;
import com.example.campusgoods.utils.UserContext;
import com.example.campusgoods.vo.GoodsVO;
import com.example.campusgoods.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 收藏接口：收藏、取消收藏、我的收藏
 */
@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /** 收藏商品 */
    @PostMapping("/{goodsId}")
    public Result<?> add(@PathVariable Long goodsId, HttpServletRequest request) {
        favoriteService.add(UserContext.getUserId(request), goodsId);
        return Result.success(null);
    }

    /** 取消收藏 */
    @DeleteMapping("/{goodsId}")
    public Result<?> remove(@PathVariable Long goodsId, HttpServletRequest request) {
        favoriteService.remove(UserContext.getUserId(request), goodsId);
        return Result.success(null);
    }

    /** 我的收藏列表 */
    @GetMapping("/list")
    public Result<List<GoodsVO>> list(HttpServletRequest request) {
        return Result.success(favoriteService.listMy(UserContext.getUserId(request)));
    }
}
