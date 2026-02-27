package com.desafio.biblioteca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuracoes de infraestrutura e definicao de beans gerenciados.
 */
@Configuration
public class AppConfig {

    /**
     * Expoe o RestTemplate como bean para permitir injecao de dependencia e mocking em testes.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}