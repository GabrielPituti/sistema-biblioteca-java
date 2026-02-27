package com.desafio.biblioteca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Definição de Beans globais para injeção de dependência gerenciada pelo Spring.
 */
@Configuration
public class AppConfig {

    /**
     * Provê uma instância compartilhada de RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}