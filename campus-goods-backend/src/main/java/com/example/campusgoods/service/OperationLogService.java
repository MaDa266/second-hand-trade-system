package com.example.campusgoods.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campusgoods.entity.OperationLog;
import com.example.campusgoods.entity.User;
import com.example.campusgoods.mapper.OperationLogMapper;
import com.example.campusgoods.mapper.UserMapper;
import com.example.campusgoods.utils.UserContext;
import com.example.campusgoods.utils.Validators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 操作日志业务
 * 各业务模块在完成写操作后调用 record(...) 记录"谁在什么时间做了什么"。
 * 记录失败只写错误日志，绝不影响主业务。
 */
@Service
public class OperationLogService extends ServiceImpl<OperationLogMapper, OperationLog> {

    private static final Logger log = LoggerFactory.getLogger(OperationLogService.class);

    /** 模块常量 */
    public static final String MODULE_AUTH = "认证";
    public static final String MODULE_GOODS = "商品";
    public static final String MODULE_MESSAGE = "留言";
    public static final String MODULE_FAVORITE = "收藏";
    public static final String MODULE_REPORT = "举报";
    public static final String MODULE_USER = "用户管理";
    public static final String MODULE_CATEGORY = "分类管理";
    public static final String MODULE_NOTICE = "公告管理";
    public static final String MODULE_LOG = "日志管理";

    /** 前端筛选用模块列表 */
    public static final List<String> MODULES = Arrays.asList(
            MODULE_AUTH, MODULE_GOODS, MODULE_MESSAGE, MODULE_FAVORITE,
            MODULE_REPORT, MODULE_USER, MODULE_CATEGORY, MODULE_NOTICE, MODULE_LOG);

    /** 详情字段最大长度，与建表语句保持一致 */
    private static final int DETAIL_MAX = 500;

    @Autowired
    private UserMapper userMapper;

    // ==================== 记录日志 ====================

    /**
     * 记录当前登录用户的操作（绝大多数场景用这个）
     */
    public void record(String module, String action, String targetType, Long targetId, String detail) {
        write(UserContext.getCurrentUserId(), null, module, action, targetType, targetId, detail);
    }

    /**
     * 指定操作人记录日志
     * 用于登录、注册这类"操作发生时还没有登录态"的场景
     */
    public void recordAs(Long userId, String username, String module, String action,
                         String targetType, Long targetId, String detail) {
        write(userId, username, module, action, targetType, targetId, detail);
    }

    private void write(Long userId, String username, String module, String action,
                       String targetType, Long targetId, String detail) {
        try {
            OperationLog entity = new OperationLog();
            entity.setUserId(userId);
            entity.setUsername(username != null ? username : resolveUsername(userId));
            entity.setModule(module);
            entity.setAction(action);
            entity.setTargetType(targetType);
            entity.setTargetId(targetId);
            entity.setDetail(truncate(detail, DETAIL_MAX));
            entity.setIp(currentIp());
            entity.setCreateTime(LocalDateTime.now());
            this.save(entity);
        } catch (Exception e) {
            // 日志写失败不能影响正常业务
            log.error("写入操作日志失败：module={}, action={}, err={}", module, action, e.getMessage());
        }
    }

    /** 解析操作人用户名：同一请求内只查一次库 */
    private String resolveUsername(Long userId) {
        if (userId == null) {
            return "匿名";
        }
        String cached = UserContext.getCurrentUsername();
        if (cached != null) {
            return cached;
        }
        User user = userMapper.selectById(userId);
        String name = user == null ? ("用户#" + userId) : user.getUsername();
        UserContext.setCurrentUsername(name);
        return name;
    }

    /** 取真实来源IP，兼容 Nginx 反向代理 */
    private String currentIp() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            HttpServletRequest request = attributes.getRequest();
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.trim().isEmpty()) {
                return forwarded.split(",")[0].trim();
            }
            String realIp = request.getHeader("X-Real-IP");
            if (realIp != null && !realIp.trim().isEmpty()) {
                return realIp.trim();
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            return null;
        }
    }

    private String truncate(String text, int max) {
        if (text == null) {
            return null;
        }
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }

    // ==================== 管理端 ====================

    /**
     * 日志分页查询：支持模块、关键字（操作人/动作/详情）、时间范围筛选
     */
    public Page<OperationLog> pageAdmin(String module, String keyword, String startTime,
                                        String endTime, Integer page, Integer size) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (module != null && !module.trim().isEmpty()) {
            wrapper.eq(OperationLog::getModule, module.trim());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            // 用 and(...) 分组，避免 OR 把筛选条件放大
            wrapper.and(w -> w.like(OperationLog::getUsername, kw)
                    .or().like(OperationLog::getAction, kw)
                    .or().like(OperationLog::getDetail, kw));
        }
        if (startTime != null && !startTime.trim().isEmpty()) {
            wrapper.ge(OperationLog::getCreateTime, parseTime(startTime, true));
        }
        if (endTime != null && !endTime.trim().isEmpty()) {
            wrapper.le(OperationLog::getCreateTime, parseTime(endTime, false));
        }
        wrapper.orderByDesc(OperationLog::getCreateTime).orderByDesc(OperationLog::getId);
        return this.page(new Page<>(Validators.page(page), Validators.size(size, 20)), wrapper);
    }

    /**
     * 修改日志备注
     * 只允许改 remark：原始的操作人、动作、时间、详情属于审计证据，不允许篡改
     */
    public void updateRemark(Long id, String remark) {
        OperationLog entity = this.getById(id);
        if (entity == null) {
            throw new RuntimeException("日志不存在");
        }
        entity.setRemark(Validators.optionalText(remark, "备注", 255));
        this.updateById(entity);
        record(MODULE_LOG, "修改日志备注", "日志", id,
                "日志#" + id + "：" + (entity.getRemark() == null || entity.getRemark().isEmpty()
                        ? "清空备注" : entity.getRemark()));
    }

    /** 删除单条日志 */
    public void delete(Long id) {
        OperationLog entity = this.getById(id);
        if (entity == null) {
            throw new RuntimeException("日志不存在");
        }
        record(MODULE_LOG, "删除日志", "日志", id,
                "删除 [" + entity.getModule() + "] " + entity.getUsername() + " 的「" + entity.getAction() + "」记录");
        this.removeById(id);
    }

    /**
     * 清理日志：beforeTime 为空表示清空全部，否则只删除该时间之前的记录
     * @return 删除条数
     */
    public int clear(String beforeTime) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        String scope = "全部";
        if (beforeTime != null && !beforeTime.trim().isEmpty()) {
            LocalDateTime before = parseTime(beforeTime, true);
            wrapper.lt(OperationLog::getCreateTime, before);
            scope = before + " 之前";
        }
        record(MODULE_LOG, "清理日志", "日志", null, "清理范围：" + scope);
        return this.getBaseMapper().delete(wrapper);
    }

    /**
     * 解析时间参数，兼容 "yyyy-MM-dd" 与 "yyyy-MM-dd HH:mm:ss"
     * 只给日期时，起始取当天 00:00:00，结束取当天 23:59:59
     */
    private LocalDateTime parseTime(String text, boolean startOfDay) {
        String value = text.trim().replace('T', ' ');
        try {
            if (value.length() <= 10) {
                LocalDate date = LocalDate.parse(value);
                return startOfDay ? date.atStartOfDay() : date.atTime(23, 59, 59);
            }
            if (value.length() == 16) {
                value = value + ":00";
            }
            return LocalDateTime.parse(value.replace(' ', 'T'));
        } catch (Exception e) {
            throw new RuntimeException("时间格式不正确，应为 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss");
        }
    }
}
