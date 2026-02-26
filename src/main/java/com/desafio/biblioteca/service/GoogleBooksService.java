package com.desafio.biblioteca.service;

import com.desafio.biblioteca.dto.GoogleBookDTO;
import com.desafio.biblioteca.dto.LivroResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleBooksService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.books.api.key:}") // Injetado via variável de ambiente ou properties
    private String apiKey;

    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    public List<LivroResponseDTO> buscarLivrosExternos(String titulo) {
        // A chave não é obrigatória para buscas simples, mas se existir, deve ser usada via variável de ambiente
        String url = BASE_URL + titulo + (apiKey.isEmpty() ? "" : "&key=" + apiKey);

        try {
            GoogleBookDTO response = restTemplate.getForObject(url, GoogleBookDTO.class);

            if (response == null || response.items() == null) return new ArrayList<>();

            return response.items().stream()
                    .map(item -> {
                        var info = item.volumeInfo();
                        String isbn = (info.industryIdentifiers() != null && !info.industryIdentifiers().isEmpty())
                                ? info.industryIdentifiers().get(0).identifier()
                                : "N/A";

                        return new LivroResponseDTO(
                                null,
                                info.title(),
                                (info.authors() != null) ? String.join(", ", info.authors()) : "Autor Desconhecido",
                                isbn,
                                null,
                                (info.categories() != null && !info.categories().isEmpty()) ? info.categories().get(0) : "Geral"
                        );
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Falha na comunicação com Google Books: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}