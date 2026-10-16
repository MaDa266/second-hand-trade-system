package com.example.campusgoods.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 通用参数校验工具
 * 校验不通过直接抛 RuntimeException，由 GlobalExceptionHandler 统一转成 Result 返回前端，
 * 避免把非法数据写进数据库（长度超限、金额越界会触发数据库异常并泄漏 SQL 细节）
 */
public class Validators {

    /** 金额上限：与建表语句 price DECIMAL(10,2) 保持一致 */
    private static final BigDecimal PRICE_MAX = new BigDecimal("999999.99");

    /** 每页最大条数：防止前端传 size=1000000 造成大结果集 */
    private static final int MAX_PAGE_SIZE = 100;

    /** 手机号格式 */
    private static final String PHONE_PATTERN = "^1[3-9]\\d{9}$";

    private Validators() {
    }

    /** 非空校验 */
    public static void notNull(Object value, String field) {
        if (value == null) {
            throw new RuntimeException(field + "不能为空");
        }
    }

    /** 必填文本：去空格后非空且不超长，返回去空格后的值 */
    public static String requiredText(String value, String field, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(field + "不能为空");
        }
        String text = value.trim();
        if (text.length() > maxLength) {
            throw new RuntimeException(field + "不能超过 " + maxLength + " 个字");
        }
        return text;
    }

    /** 选填文本：为空返回 null（MyBatis-Plus 会跳过 null 字段），超长报错 */
    public static String optionalText(String value, String field, int maxLength) {
        if (value == null) {
            return null;
        }
        String text = value.trim();
        if (text.length() > maxLength) {
            throw new RuntimeException(field + "不能超过 " + maxLength + " 个字");
        }
        return text;
    }

    /** 选填手机号：为空返回 null，否则必须是 11 位大陆手机号 */
    public static String optionalPhone(String value) {
        String phone = optionalText(value, "手机号", 20);
        if (phone != null && !phone.isEmpty() && !phone.matches(PHONE_PATTERN)) {
            throw new RuntimeException("手机号格式不正确");
        }
        return phone;
    }

    /** 商品价格：必须大于 0 且不超过 DECIMAL(10,2) 上限，统一保留两位小数 */
    public static BigDecimal requiredPrice(BigDecimal value, String field) {
        if (value == null) {
            throw new RuntimeException(field + "不能为空");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(field + "必须大于 0");
        }
        return checkMax(value, field);
    }

    /** 选填价格（原价）：为空返回 null，否则 0 ~ 上限 */
    public static BigDecimal optionalPrice(BigDecimal value, String field) {
        if (value == null) {
            return null;
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException(field + "不能为负数");
        }
        return checkMax(value, field);
    }

    private static BigDecimal checkMax(BigDecimal value, String field) {
        if (value.compareTo(PRICE_MAX) > 0) {
            throw new RuntimeException(field + "不能超过 " + PRICE_MAX);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    /** 页码：小于 1 或为空时归一到 1 */
    public static int page(Integer page) {
        if (page == null || page < 1) {
            return 1;
        }
        return page;
    }

    /** 每页条数：限制在 1 ~ 100，为空时用默认值 */
    public static int size(Integer size, int defaultSize) {
        if (size == null || size < 1) {
            return defaultSize;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }
}
