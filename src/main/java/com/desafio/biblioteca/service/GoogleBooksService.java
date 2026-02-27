package com.desafio.biblioteca.service;

import com.desafio.biblioteca.dto.GoogleBookDTO;
import com.desafio.biblioteca.dto.LivroResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Integração externa com a Google Books API para descoberta de obras.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleBooksService {

    private final RestTemplate restTemplate;

    @Value("${google.books.api.key:}")
    private String apiKey;

    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    public List<LivroResponseDTO> buscarLivrosExternos(String query) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .queryParam("q", query)
                    .queryParam("key", apiKey)
                    .toUriString();

            GoogleBookDTO response = restTemplate.getForObject(url, GoogleBookDTO.class);

            if (response == null || response.items() == null) {
                return Collections.emptyList();
            }

            return response.items().stream()
                    .map(item -> new LivroResponseDTO(
                            null,
                            item.volumeInfo().title(),
                            item.volumeInfo().authors() != null ? String.join(", ", item.volumeInfo().authors()) : "Autor Desconhecido",
                            item.volumeInfo().industryIdentifiers() != null ? item.volumeInfo().industryIdentifiers().get(0).identifier() : "N/A",
                            null,
                            item.volumeInfo().categories() != null ? item.volumeInfo().categories().get(0) : "Geral"
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Falha na comunicação com Google Books: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}