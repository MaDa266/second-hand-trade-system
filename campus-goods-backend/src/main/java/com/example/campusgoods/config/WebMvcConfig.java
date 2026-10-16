package com.example.campusgoods.config;

import com.example.campusgoods.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：注册鉴权拦截器 + 上传文件静态资源映射
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /uploads/** 映射到本地 uploads 目录（用户上传的文件）
        String uploadPath = System.getProperty("user.dir").replace("\\", "/") + "/uploads/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);

        // 商品封面图统一存放在项目根目录的 images 文件夹
        // 此处将 /uploads/goods/** 映射到该目录，保持数据库中 /uploads/goods/gN.jpg 路径不变
        String goodsPath = new java.io.File(System.getProperty("user.dir"), "../images")
                .getAbsolutePath().replace("\\", "/") + "/";
        registry.addResourceHandler("/uploads/goods/**")
                .addResourceLocations("file:" + goodsPath);
    }
}
