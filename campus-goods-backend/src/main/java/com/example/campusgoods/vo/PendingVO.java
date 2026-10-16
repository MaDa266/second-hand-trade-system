package com.example.campusgoods.vo;

import lombok.Data;

import java.util.List;

/**
 * 管理员待办提醒视图对象
 * 管理员登录后前端拉取一次，用于弹出待审核商品 / 待处理举报提醒
 */
@Data
public class PendingVO {

    /** 待审核商品数量 */
    private Long pendingGoodsCount = 0L;

    /** 待处理举报数量 */
    private Long pendingReportsCount = 0L;

    /** 待审核商品（最多展示若干条） */
    private List<GoodsVO> pendingGoods;

    /** 待处理举报（最多展示若干条） */
    private List<ReportVO> pendingReports;

    /** 待办总数 */
    public Long getTotalCount() {
        return (pendingGoodsCount == null ? 0L : pendingGoodsCount)
                + (pendingReportsCount == null ? 0L : pendingReportsCount);
    }
}
