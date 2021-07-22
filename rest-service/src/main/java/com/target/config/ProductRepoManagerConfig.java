package com.target.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import com.target.repo.ProductRepoManager;

@Configuration
public class ProductRepoManagerConfig {
    @Bean
    public ProductRepoManager dataRepoManager() {
        return new ProductRepoManager();
    }
}