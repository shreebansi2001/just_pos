package com.crmportal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	 
    @Value("${app.image.path}")
    private String uploadRoot;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadRoot);
                
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadRoot)
                .setCachePeriod(3600);
        
        registry.addResourceHandler("/resources/**")
        .addResourceLocations("file:" + uploadRoot)
        .setCachePeriod(3600);
    }
}
