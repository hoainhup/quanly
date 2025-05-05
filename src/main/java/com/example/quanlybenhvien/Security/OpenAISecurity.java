package com.example.quanlybenhvien.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
@Order(4)
public class OpenAISecurity {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
