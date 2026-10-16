package com.example.campusgoods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 * 记录"谁在什么时间做了什么"，用于管理员审计
 */
@Data
@TableName("operation_log")
public class OperationLog {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作人ID */
    private Long userId;

    /** 操作人用户名（冗余保存，用户被删除后仍可追溯） */
    private String username;

    /** 所属模块：认证 / 商品 / 留言 / 收藏 / 举报 / 用户管理 / 分类管理 / 公告管理 / 日志管理 */
    private String module;

    /** 操作动作：发布商品 / 审核通过 ... */
    private String action;

    /** 操作对象类型：商品 / 留言 / 用户 ... */
    private String targetType;

    /** 操作对象ID */
    private Long targetId;

    /** 操作详情 */
    private String detail;

    /** 管理员备注（唯一允许修改的字段，保证原始记录不可篡改） */
    private String remark;

    /** 操作来源IP */
    private String ip;

    /** 操作时间 */
    private LocalDateTime createTime;
}
