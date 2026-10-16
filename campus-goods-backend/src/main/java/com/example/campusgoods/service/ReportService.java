package com.example.campusgoods.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campusgoods.dto.ReportDTO;
import com.example.campusgoods.entity.Goods;
import com.example.campusgoods.entity.Report;
import com.example.campusgoods.entity.User;
import com.example.campusgoods.mapper.GoodsMapper;
import com.example.campusgoods.mapper.ReportMapper;
import com.example.campusgoods.mapper.UserMapper;
import com.example.campusgoods.utils.Validators;
import com.example.campusgoods.vo.ReportVO;
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
 * 举报业务
 */
@Service
public class ReportService extends ServiceImpl<ReportMapper, Report> {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OperationLogService operationLogService;

    /**
     * 提交举报：状态待处理；同一用户对同一商品只允许一条待处理举报
     */
    public void submit(Long userId, ReportDTO dto) {
        Validators.notNull(userId, "登录状态");
        Validators.notNull(dto.getGoodsId(), "商品");
        String reason = Validators.requiredText(dto.getReason(), "举报原因", 255);

        Goods goods = goodsMapper.selectById(dto.getGoodsId());
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }
        if (userId.equals(goods.getUserId())) {
            throw new RuntimeException("不能举报自己发布的商品");
        }
        Long count = this.lambdaQuery()
                .eq(Report::getGoodsId, dto.getGoodsId())
                .eq(Report::getUserId, userId)
                .eq(Report::getStatus, 0)
                .count();
        if (count != null && count > 0) {
            throw new RuntimeException("您已举报过该商品，请等待管理员处理");
        }

        Report report = new Report();
        report.setGoodsId(dto.getGoodsId());
        report.setUserId(userId);
        report.setReason(reason);
        report.setStatus(0);
        report.setCreateTime(LocalDateTime.now());
        this.save(report);
        operationLogService.record(OperationLogService.MODULE_REPORT, "提交举报", "商品", dto.getGoodsId(),
                "举报商品「" + goods.getTitle() + "」，原因：" + reason);
    }

    /**
     * 管理端举报分页：举报时间倒序，附带商品标题与举报人昵称
     */
    public Page<ReportVO> pageAdmin(Integer page, Integer size) {
        Page<Report> reportPage = this.page(new Page<>(Validators.page(page), Validators.size(size, 10)),
                new LambdaQueryWrapper<Report>().orderByDesc(Report::getCreateTime));
        Page<ReportVO> voPage = new Page<>(reportPage.getCurrent(), reportPage.getSize(), reportPage.getTotal());
        voPage.setRecords(toVOList(reportPage.getRecords()));
        return voPage;
    }

    /** 待处理举报数量（管理员登录提醒用） */
    public Long countPending() {
        Long count = this.lambdaQuery().eq(Report::getStatus, 0).count();
        return count == null ? 0L : count;
    }

    /** 待处理举报列表（管理员登录提醒用，最多 limit 条） */
    public List<ReportVO> listPending(int limit) {
        Page<Report> page = new Page<>(1, limit);
        page.setSearchCount(false);
        Page<Report> result = this.page(page, new LambdaQueryWrapper<Report>()
                .eq(Report::getStatus, 0)
                .orderByDesc(Report::getCreateTime));
        return toVOList(result.getRecords());
    }

    /**
     * 举报批量转 VO：一次性查出商品标题与举报人昵称，避免循环查库
     */
    public List<ReportVO> toVOList(List<Report> records) {
        List<ReportVO> voList = new ArrayList<>();
        if (records == null || records.isEmpty()) {
            return voList;
        }
        Set<Long> goodsIds = new HashSet<>();
        Set<Long> userIds = new HashSet<>();
        for (Report r : records) {
            goodsIds.add(r.getGoodsId());
            userIds.add(r.getUserId());
        }
        Map<Long, Goods> goodsMap = goodsIds.isEmpty() ? Collections.emptyMap()
                : goodsMapper.selectBatchIds(goodsIds).stream()
                    .collect(Collectors.toMap(Goods::getId, g -> g));
        Map<Long, User> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        for (Report r : records) {
            ReportVO vo = new ReportVO();
            vo.setId(r.getId());
            vo.setGoodsId(r.getGoodsId());
            Goods goods = goodsMap.get(r.getGoodsId());
            vo.setGoodsTitle(goods == null ? "商品已删除" : goods.getTitle());
            vo.setUserId(r.getUserId());
            User user = userMap.get(r.getUserId());
            vo.setUserName(user == null ? "未知用户"
                    : (user.getNickname() != null && !user.getNickname().isEmpty()
                        ? user.getNickname() : user.getUsername()));
            vo.setReason(r.getReason());
            vo.setStatus(r.getStatus());
            vo.setCreateTime(r.getCreateTime());
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 处理举报：标记已处理；offline=true 时同时下架被举报商品
     * 两步写操作放在同一事务里，避免只成功一半
     */
    @Transactional(rollbackFor = Exception.class)
    public void handle(Long id, Boolean offline) {
        Report report = this.getById(id);
        if (report == null) {
            throw new RuntimeException("举报记录不存在");
        }
        if (report.getStatus() != null && report.getStatus() == 1) {
            throw new RuntimeException("该举报已处理，请勿重复操作");
        }
        report.setStatus(1);
        this.updateById(report);
        if (Boolean.TRUE.equals(offline)) {
            Goods goods = goodsMapper.selectById(report.getGoodsId());
            if (goods != null) {
                goods.setStatus(3);
                goods.setUpdateTime(LocalDateTime.now());
                goodsMapper.updateById(goods);
            }
        }
        operationLogService.record(OperationLogService.MODULE_REPORT, "处理举报", "举报", id,
                "举报#" + id + "（商品#" + report.getGoodsId() + "）"
                        + (Boolean.TRUE.equals(offline) ? "，已同时下架商品" : "，仅标记已处理"));
    }
}
