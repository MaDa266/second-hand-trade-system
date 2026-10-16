package com.example.campusgoods.controller;

import com.example.campusgoods.entity.Notice;
import com.example.campusgoods.service.NoticeService;
import com.example.campusgoods.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公告接口（前台，公开）：首页公告栏展示
 */
@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /** 最新公告列表（公开） */
    @GetMapping("/list")
    public Result<List<Notice>> list() {
        return Result.success(noticeService.listLatest());
    }
}
