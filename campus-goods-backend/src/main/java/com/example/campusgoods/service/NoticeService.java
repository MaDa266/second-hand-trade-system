package com.example.campusgoods.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campusgoods.entity.Notice;
import com.example.campusgoods.mapper.NoticeMapper;
import com.example.campusgoods.utils.Validators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告业务
 */
@Service
public class NoticeService extends ServiceImpl<NoticeMapper, Notice> {

    /** 首页公告最多展示条数 */
    private static final int HOME_LIMIT = 5;

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 前台公告列表：按发布时间倒序取最新若干条，不分页
     */
    public List<Notice> listLatest() {
        Page<Notice> page = new Page<>(1, HOME_LIMIT);
        // 前台只关心数据，不需要额外发一条 count 查询
        page.setSearchCount(false);
        return this.page(page, new LambdaQueryWrapper<Notice>().orderByDesc(Notice::getCreateTime))
                .getRecords();
    }

    /**
     * 管理端公告分页：按发布时间倒序
     */
    public Page<Notice> pageAdmin(Integer page, Integer size) {
        return this.page(new Page<>(Validators.page(page), Validators.size(size, 10)),
                new LambdaQueryWrapper<Notice>().orderByDesc(Notice::getCreateTime));
    }

    /**
     * 新增公告
     */
    public void create(String title, String content) {
        Notice notice = new Notice();
        notice.setTitle(Validators.requiredText(title, "公告标题", 100));
        notice.setContent(Validators.optionalText(content, "公告内容", 5000));
        notice.setCreateTime(LocalDateTime.now());
        this.save(notice);
        operationLogService.record(OperationLogService.MODULE_NOTICE, "发布公告", "公告", notice.getId(),
                "标题：" + notice.getTitle());
    }

    /**
     * 修改公告
     */
    public void update(Long id, String title, String content) {
        Notice db = this.getById(id);
        if (db == null) {
            throw new RuntimeException("公告不存在");
        }
        db.setTitle(Validators.requiredText(title, "公告标题", 100));
        db.setContent(Validators.optionalText(content, "公告内容", 5000));
        this.updateById(db);
        operationLogService.record(OperationLogService.MODULE_NOTICE, "修改公告", "公告", id,
                "标题：" + db.getTitle());
    }

    /**
     * 删除公告
     */
    public void delete(Long id) {
        Notice db = this.getById(id);
        if (db == null) {
            throw new RuntimeException("公告不存在");
        }
        this.removeById(id);
        operationLogService.record(OperationLogService.MODULE_NOTICE, "删除公告", "公告", id,
                "标题：" + db.getTitle());
    }
}
