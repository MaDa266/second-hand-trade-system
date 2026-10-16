package com.example.campusgoods.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campusgoods.dto.AuditDTO;
import com.example.campusgoods.dto.LogRemarkDTO;
import com.example.campusgoods.dto.ReportHandleDTO;
import com.example.campusgoods.entity.Category;
import com.example.campusgoods.entity.Goods;
import com.example.campusgoods.entity.Notice;
import com.example.campusgoods.entity.OperationLog;
import com.example.campusgoods.entity.User;
import com.example.campusgoods.service.CategoryService;
import com.example.campusgoods.service.GoodsService;
import com.example.campusgoods.service.NoticeService;
import com.example.campusgoods.service.OperationLogService;
import com.example.campusgoods.service.ReportService;
import com.example.campusgoods.service.UserService;
import com.example.campusgoods.utils.Validators;
import com.example.campusgoods.vo.GoodsVO;
import com.example.campusgoods.vo.PendingVO;
import com.example.campusgoods.vo.ReportVO;
import com.example.campusgoods.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员接口：用户管理、分类管理、商品审核、举报管理、公告管理、操作日志、数据统计
 * （拦截器已校验 role=0，学生访问一律 403）
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    /** 待办提醒弹窗里最多预览的条数 */
    private static final int PENDING_PREVIEW_LIMIT = 5;

    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private OperationLogService operationLogService;

    // ==================== 用户管理 ====================

    /** 用户分页列表：keyword 对用户名/昵称模糊，id 倒序，不返回密码 */
    @GetMapping("/users")
    public Result<Page<User>> users(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            // 用 and(...) 分组，避免后续追加条件时 OR 的优先级把查询条件放大
            wrapper.and(w -> w.like(User::getUsername, kw).or().like(User::getNickname, kw));
        }
        wrapper.orderByDesc(User::getId);
        Page<User> userPage = userService.page(
                new Page<>(Validators.page(page), Validators.size(size, 10)), wrapper);
        if (userPage.getRecords() != null) {
            userPage.getRecords().forEach(u -> u.setPassword(null));
        }
        return Result.success(userPage);
    }

    /** 启用 / 禁用用户（管理员账号不允许被禁用） */
    @PutMapping("/users/{id}/status")
    public Result<?> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态参数不正确");
        }
        User db = userService.getById(id);
        if (db == null) {
            return Result.error("用户不存在");
        }
        if (db.getRole() != null && db.getRole() == 0) {
            return Result.error("不能禁用管理员账号");
        }
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userService.updateById(user);
        operationLogService.record(OperationLogService.MODULE_USER,
                status == 1 ? "启用用户" : "禁用用户", "用户", id, "目标用户：" + db.getUsername());
        return Result.success(null);
    }

    /** 重置用户密码为 123456（管理员账号不允许重置） */
    @PutMapping("/users/{id}/password")
    public Result<?> resetPassword(@PathVariable Long id) {
        User db = userService.getById(id);
        if (db == null) {
            return Result.error("用户不存在");
        }
        if (db.getRole() != null && db.getRole() == 0) {
            return Result.error("不能重置管理员密码");
        }
        User user = new User();
        user.setId(id);
        user.setPassword("123456");
        userService.updateById(user);
        operationLogService.record(OperationLogService.MODULE_USER, "重置用户密码", "用户", id,
                "目标用户：" + db.getUsername() + "，密码已重置为 123456");
        return Result.success(null);
    }

    // ==================== 分类管理 ====================

    /** 全部分类列表 */
    @GetMapping("/category")
    public Result<List<Category>> categoryList() {
        return Result.success(categoryService.listAll());
    }

    /** 新增分类 */
    @PostMapping("/category")
    public Result<?> addCategory(@RequestBody Category category) {
        categoryService.create(category.getName(), category.getSort(), category.getStatus());
        return Result.success(null);
    }

    /** 修改分类 */
    @PutMapping("/category/{id}")
    public Result<?> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        categoryService.update(id, category.getName(), category.getSort(), category.getStatus());
        return Result.success(null);
    }

    /** 删除分类：分类下存在商品时拒绝 */
    @DeleteMapping("/category/{id}")
    public Result<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success(null);
    }

    // ==================== 商品审核 ====================

    /** 商品审核分页：status 为空查全部，否则按状态查 */
    @GetMapping("/goods/audit")
    public Result<Page<GoodsVO>> auditList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(goodsService.pageAudit(status, page, size));
    }

    /** 审核商品：status=1 通过 / 4 不通过 */
    @PutMapping("/goods/{id}/audit")
    public Result<?> audit(@PathVariable Long id, @RequestBody AuditDTO dto) {
        if (dto.getStatus() == null || (dto.getStatus() != 1 && dto.getStatus() != 4)) {
            return Result.error("审核状态不正确");
        }
        goodsService.audit(id, dto.getStatus());
        return Result.success(null);
    }

    /** 下架违规商品 */
    @PutMapping("/goods/{id}/offline")
    public Result<?> offline(@PathVariable Long id) {
        goodsService.offline(id);
        return Result.success(null);
    }

    // ==================== 举报管理 ====================

    /** 举报分页列表 */
    @GetMapping("/report")
    public Result<Page<ReportVO>> reportList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reportService.pageAdmin(page, size));
    }

    /** 处理举报：offline=true 时同时下架商品 */
    @PutMapping("/report/{id}")
    public Result<?> handleReport(@PathVariable Long id, @RequestBody ReportHandleDTO dto) {
        reportService.handle(id, dto.getOffline());
        return Result.success(null);
    }

    // ==================== 公告管理 ====================

    /** 公告分页列表 */
    @GetMapping("/notice")
    public Result<Page<Notice>> noticeList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(noticeService.pageAdmin(page, size));
    }

    /** 新增公告 */
    @PostMapping("/notice")
    public Result<?> addNotice(@RequestBody Notice notice) {
        noticeService.create(notice.getTitle(), notice.getContent());
        return Result.success(null);
    }

    /** 修改公告 */
    @PutMapping("/notice/{id}")
    public Result<?> updateNotice(@PathVariable Long id, @RequestBody Notice notice) {
        noticeService.update(id, notice.getTitle(), notice.getContent());
        return Result.success(null);
    }

    /** 删除公告 */
    @DeleteMapping("/notice/{id}")
    public Result<?> deleteNotice(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.success(null);
    }

    // ==================== 操作日志 ====================

    /** 日志模块下拉选项 */
    @GetMapping("/log/modules")
    public Result<List<String>> logModules() {
        return Result.success(OperationLogService.MODULES);
    }

    /** 日志分页查询：支持模块、关键字（操作人/动作/详情）、时间范围 */
    @GetMapping("/log")
    public Result<Page<OperationLog>> logList(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(operationLogService.pageAdmin(module, keyword, startTime, endTime, page, size));
    }

    /** 修改日志备注（原始记录不可篡改） */
    @PutMapping("/log/{id}/remark")
    public Result<?> updateLogRemark(@PathVariable Long id, @RequestBody LogRemarkDTO dto) {
        operationLogService.updateRemark(id, dto.getRemark());
        return Result.success(null);
    }

    /** 删除单条日志 */
    @DeleteMapping("/log/{id}")
    public Result<?> deleteLog(@PathVariable Long id) {
        operationLogService.delete(id);
        return Result.success(null);
    }

    /** 清理日志：beforeTime 为空表示清空全部，否则只清理该时间之前的记录 */
    @DeleteMapping("/log")
    public Result<Map<String, Integer>> clearLog(@RequestParam(required = false) String beforeTime) {
        int deleted = operationLogService.clear(beforeTime);
        Map<String, Integer> data = new HashMap<>();
        data.put("deleted", deleted);
        return Result.success(data);
    }

    // ==================== 待办提醒 ====================

    /**
     * 管理员待办提醒：待审核商品 + 待处理举报
     * 管理员登录后前端调用一次，按类型分别弹出提醒
     */
    @GetMapping("/pending")
    public Result<PendingVO> pending() {
        PendingVO vo = new PendingVO();
        Page<GoodsVO> goodsPage = goodsService.pageAudit(0, 1, PENDING_PREVIEW_LIMIT);
        vo.setPendingGoodsCount(goodsPage.getTotal());
        vo.setPendingGoods(goodsPage.getRecords());
        vo.setPendingReportsCount(reportService.countPending());
        vo.setPendingReports(reportService.listPending(PENDING_PREVIEW_LIMIT));
        return Result.success(vo);
    }

    // ==================== 数据统计 ====================

    /** 统计：用户总数、商品总数、待审核商品数、已售商品数、待处理举报数 */
    @GetMapping("/statistics")
    public Result<Map<String, Long>> statistics() {
        Map<String, Long> data = new HashMap<>();
        data.put("userTotal", userService.count());
        data.put("goodsTotal", goodsService.count());
        data.put("pendingTotal", goodsService.lambdaQuery().eq(Goods::getStatus, 0).count());
        data.put("soldTotal", goodsService.lambdaQuery().eq(Goods::getStatus, 2).count());
        return Result.success(data);
    }
}
