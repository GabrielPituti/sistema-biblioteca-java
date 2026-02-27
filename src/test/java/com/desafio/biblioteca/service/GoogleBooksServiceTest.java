package com.desafio.biblioteca.service;

import com.desafio.biblioteca.dto.GoogleBookDTO;
import com.desafio.biblioteca.dto.LivroResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Teste unitario deterministico para validacao do mapeamento da API externa.
 */
@ExtendWith(MockitoExtension.class)
class GoogleBooksServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GoogleBooksService googleBooksService;

    @Test
    @DisplayName("Deve converter o formato JSON do Google para o modelo interno da biblioteca")
    void deveMapearRespostaDaApi() {
        var identifier = new GoogleBookDTO.IndustryIdentifier("ISBN_13", "9780596517748");
        var volumeInfo = new GoogleBookDTO.VolumeInfo(
                "Java Senior Guide", List.of("Tech Lead"), null, null, null,
                List.of(identifier), List.of("Tecnologia")
        );
        var mockResponse = new GoogleBookDTO(List.of(new GoogleBookDTO.Item(volumeInfo)));

        when(restTemplate.getForObject(anyString(), eq(GoogleBookDTO.class)))
                .thenReturn(mockResponse);

        List<LivroResponseDTO> resultado = googleBooksService.buscarLivrosExternos("java");

        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals("Java Senior Guide", resultado.get(0).titulo()),
                () -> assertEquals("9780596517748", resultado.get(0).isbn()),
                () -> assertEquals("Tecnologia", resultado.get(0).categoria())
        );
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando a API retornar resposta nula")
    void deveRetornarVazioQuandoApiRetornaNulo() {
        when(restTemplate.getForObject(anyString(), eq(GoogleBookDTO.class)))
                .thenReturn(null);

        List<LivroResponseDTO> resultado = googleBooksService.buscarLivrosExternos("test");

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando a API retornar itens nulos")
    void deveRetornarVazioQuandoItensForemNulos() {
        when(restTemplate.getForObject(anyString(), eq(GoogleBookDTO.class)))
                .thenReturn(new GoogleBookDTO(null));

        List<LivroResponseDTO> resultado = googleBooksService.buscarLivrosExternos("test");

        assertTrue(resultado.isEmpty());
    }
}