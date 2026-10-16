package com.example.campusgoods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园二手物品交易管理系统 启动类
 */
@SpringBootApplication
@MapperScan("com.example.campusgoods.mapper")
public class CampusGoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusGoodsApplication.class, args);
    }
}
