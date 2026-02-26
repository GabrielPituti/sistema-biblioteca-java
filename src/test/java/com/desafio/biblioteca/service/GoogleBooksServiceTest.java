package com.desafio.biblioteca.service;

import com.desafio.biblioteca.dto.LivroResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class GoogleBooksServiceTest {

    @Autowired
    private GoogleBooksService googleBooksService;

    @Test
    @DisplayName("Deve retornar uma lista de livros ao pesquisar por termo válido na API Google")
    void deveRetornarLivrosAoPesquisar() {
        List<LivroResponseDTO> resultados = googleBooksService.buscarLivrosExternos("Java");

        assertNotNull(resultados);
        if (!resultados.isEmpty()) {
            assertNotNull(resultados.get(0).titulo());
            assertNotNull(resultados.get(0).isbn());
        }
    }
}