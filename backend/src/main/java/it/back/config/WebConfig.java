package it.back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // '/upload/**' URL 패턴으로 요청이 오면
        // 'file:///home/Loute/ourshop/' 디렉토리에서 파일을 찾아 제공합니다.
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}