package it.back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.MultipartConfigElement;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 상품 이미지 경로: /product/** 요청을 C:/ourshop/product/ 폴더로 매핑
        String productResourcePath = "file:" + uploadDir + "/product/";
        registry.addResourceHandler("/product/**")
                .addResourceLocations(productResourcePath)
                .setCachePeriod(0)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        // 임시 이미지 경로: /temp/** 요청을 C:/ourshop/temp/ 폴더로 매핑
        String tempResourcePath = "file:" + uploadDir + "/temp/";
        registry.addResourceHandler("/temp/**")
                .addResourceLocations(tempResourcePath)
                .setCachePeriod(0)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

    }

    //파일제한
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.of(50, DataUnit.MEGABYTES));
        factory.setMaxRequestSize(DataSize.of(50, DataUnit.MEGABYTES));
        return factory.createMultipartConfig();
    }
}
