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
 * Integracao com provedor externo para catalogacao automatica de obras.
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
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .queryParam("q", query);

            if (apiKey != null && !apiKey.isBlank()) {
                builder.queryParam("key", apiKey);
            }

            String url = builder.toUriString();
            GoogleBookDTO response = restTemplate.getForObject(url, GoogleBookDTO.class);

            if (response == null || response.items() == null) {
                return Collections.emptyList();
            }

            return response.items().stream()
                    .map(this::mapearParaDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Falha na integracao externa: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private LivroResponseDTO mapearParaDTO(GoogleBookDTO.Item item) {
        var info = item.volumeInfo();
        String autores = info.authors() != null ? String.join(", ", info.authors()) : "Autor Desconhecido";
        String isbn = (info.industryIdentifiers() != null && !info.industryIdentifiers().isEmpty())
                ? info.industryIdentifiers().get(0).identifier()
                : "N/A";
        String categoria = (info.categories() != null && !info.categories().isEmpty())
                ? info.categories().get(0)
                : "Geral";

        return new LivroResponseDTO(null, info.title(), autores, isbn, null, categoria);
    }
}