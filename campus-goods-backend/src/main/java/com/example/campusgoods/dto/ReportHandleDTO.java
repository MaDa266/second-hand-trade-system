package com.example.campusgoods.dto;

import lombok.Data;

/**
 * 举报处理参数：offline=true 下架对应商品，false 仅标记已处理
 */
@Data
public class ReportHandleDTO {

    /** 是否下架被举报商品 */
    private Boolean offline;
}
