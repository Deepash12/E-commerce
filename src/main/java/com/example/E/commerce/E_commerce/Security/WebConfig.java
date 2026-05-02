package com.example.E.commerce.E_commerce.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer
{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {

        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations("file:C:/Users/deepa/OneDrive/Desktop/Project/E-commerce/uploads/");
                .addResourceLocations("file:C:/Projects/E-commerce/uploads/");
    }
}
