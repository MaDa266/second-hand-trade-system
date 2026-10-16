package com.example.campusgoods.controller;

import com.example.campusgoods.dto.MessageDTO;
import com.example.campusgoods.service.MessageService;
import com.example.campusgoods.utils.UserContext;
import com.example.campusgoods.vo.MessageVO;
import com.example.campusgoods.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 留言接口：发送留言 / 回复、商品留言板、我的留言
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /** 发送留言或回复：toUserId 为空时默认发给商品发布人 */
    @PostMapping
    public Result<?> send(@RequestBody MessageDTO dto, HttpServletRequest request) {
        messageService.send(UserContext.getUserId(request), dto);
        return Result.success(null);
    }

    /** 商品留言板（公开）：该商品下的全部留言 */
    @GetMapping("/goods/{goodsId}")
    public Result<List<MessageVO>> listByGoods(@PathVariable Long goodsId) {
        return Result.success(messageService.listByGoods(goodsId));
    }

    /** 我的留言列表（我发出的 + 我收到的） */
    @GetMapping("/list")
    public Result<List<MessageVO>> list(HttpServletRequest request) {
        return Result.success(messageService.listMy(UserContext.getUserId(request)));
    }
}
