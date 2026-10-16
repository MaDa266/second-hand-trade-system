package com.example.campusgoods.controller;

import com.example.campusgoods.dto.ReportDTO;
import com.example.campusgoods.service.ReportService;
import com.example.campusgoods.utils.UserContext;
import com.example.campusgoods.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 举报接口（学生端）：提交举报
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /** 提交举报 */
    @PostMapping
    public Result<?> submit(@RequestBody ReportDTO dto, HttpServletRequest request) {
        reportService.submit(UserContext.getUserId(request), dto);
        return Result.success(null);
    }
}
