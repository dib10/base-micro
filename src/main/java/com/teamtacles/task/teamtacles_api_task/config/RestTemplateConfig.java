package com.teamtacles.task.teamtacles_api_task.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // A linha abaixo foi corrigida para usar o nome correto da propriedade
    @Value("${application.client.urls.user-service}")
    private String monolithBaseUrl;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // Você pode configurar o RestTemplate aqui, se necessário.
        return restTemplate;
    }
}