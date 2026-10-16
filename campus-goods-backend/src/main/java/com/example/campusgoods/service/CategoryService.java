package com.example.campusgoods.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campusgoods.entity.Category;
import com.example.campusgoods.entity.Goods;
import com.example.campusgoods.mapper.CategoryMapper;
import com.example.campusgoods.mapper.GoodsMapper;
import com.example.campusgoods.utils.Validators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类业务
 */
@Service
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private OperationLogService operationLogService;

    /**
     * 启用分类列表（首页分类筛选，公开接口）：status=1，sort 升序
     */
    public List<Category> listEnabled() {
        return this.lambdaQuery().eq(Category::getStatus, 1).orderByAsc(Category::getSort).list();
    }

    /**
     * 全部分类列表（管理端）：sort 升序
     */
    public List<Category> listAll() {
        return this.lambdaQuery().orderByAsc(Category::getSort).list();
    }

    /**
     * 新增分类：名称必填且不能重复
     */
    public void create(String name, Integer sort, Integer status) {
        String trimName = Validators.requiredText(name, "分类名称", 50);
        checkNameUnique(trimName, null);

        Category category = new Category();
        category.setName(trimName);
        category.setSort(sort == null ? 0 : sort);
        category.setStatus(status == null ? 1 : status);
        category.setCreateTime(LocalDateTime.now());
        this.save(category);
        operationLogService.record(OperationLogService.MODULE_CATEGORY, "新增分类", "分类", category.getId(),
                "分类名称：" + category.getName());
    }

    /**
     * 修改分类：名称必填且不能与其它分类重复
     */
    public void update(Long id, String name, Integer sort, Integer status) {
        Category db = this.getById(id);
        if (db == null) {
            throw new RuntimeException("分类不存在");
        }
        String trimName = Validators.requiredText(name, "分类名称", 50);
        checkNameUnique(trimName, id);

        db.setName(trimName);
        if (sort != null) {
            db.setSort(sort);
        }
        if (status != null) {
            db.setStatus(status);
        }
        this.updateById(db);
        operationLogService.record(OperationLogService.MODULE_CATEGORY, "修改分类", "分类", id,
                "名称：" + db.getName() + "，排序：" + db.getSort()
                        + "，状态：" + (db.getStatus() != null && db.getStatus() == 1 ? "启用" : "停用"));
    }

    /**
     * 删除分类：分类下存在商品时拒绝删除
     */
    public void deleteCategory(Long id) {
        Category db = this.getById(id);
        if (db == null) {
            throw new RuntimeException("分类不存在");
        }
        Long count = goodsMapper.selectCount(new LambdaQueryWrapper<Goods>().eq(Goods::getCategoryId, id));
        if (count != null && count > 0) {
            throw new RuntimeException("该分类下存在商品，无法删除");
        }
        this.removeById(id);
        operationLogService.record(OperationLogService.MODULE_CATEGORY, "删除分类", "分类", id,
                "分类名称：" + db.getName());
    }

    /** 分类名称查重，excludeId 用于修改时排除自身 */
    private void checkNameUnique(String name, Long excludeId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<Category>().eq(Category::getName, name);
        if (excludeId != null) {
            wrapper.ne(Category::getId, excludeId);
        }
        Long count = this.count(wrapper);
        if (count != null && count > 0) {
            throw new RuntimeException("分类名称已存在");
        }
    }
}
