package com.vin.VinSystem.Config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Dùng toUri().toString() thay vì ghép chuỗi thủ công
        // → tự động xử lý Windows backslash và đảm bảo trailing slash
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String resourceLocation = uploadPath.toUri().toString();

        // Đảm bảo luôn có trailing slash (Spring yêu cầu)
        if (!resourceLocation.endsWith("/")) {
            resourceLocation = resourceLocation + "/";
        }

        System.out.println("[WebConfig] Serving /uploads/** from: " + resourceLocation);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation)
                .setCachePeriod(3600); // cache 1 giờ, bỏ dòng này nếu muốn no-cache lúc dev
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}