package com.desafio.biblioteca.service;

import com.desafio.biblioteca.dto.GoogleBookDTO;
import com.desafio.biblioteca.dto.LivroResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável por consumir dados da Google Books API.
 * * Implementa a lógica de busca externa e conversão de dados estrangeiros
 * para o formato compatível com o acervo local da biblioteca.
 */
@Service
@RequiredArgsConstructor
public class GoogleBooksService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    /**
     * Consulta a API do Google Books por título e transforma o resultado em DTOs locais.
     * * @param titulo Termo de pesquisa (título do livro).
     * @return Lista de obras encontradas formatadas para a interface do sistema.
     */
    public List<LivroResponseDTO> buscarLivrosExternos(String titulo) {
        String url = GOOGLE_BOOKS_URL + titulo;

        try {
            GoogleBookDTO response = restTemplate.getForObject(url, GoogleBookDTO.class);

            if (response == null || response.items() == null) {
                return new ArrayList<>();
            }

            return response.items().stream()
                    .map(item -> {
                        var info = item.volumeInfo();
                        String isbn = (info.industryIdentifiers() != null && !info.industryIdentifiers().isEmpty())
                                ? info.industryIdentifiers().get(0).identifier()
                                : "N/A";

                        String autor = (info.authors() != null) ? String.join(", ", info.authors()) : "Autor Desconhecido";
                        String categoria = (info.categories() != null && !info.categories().isEmpty())
                                ? info.categories().get(0)
                                : "Geral";

                        return new LivroResponseDTO(
                                null,
                                info.title(),
                                autor,
                                isbn,
                                null, // Data omitida para preenchimento manual no cadastro
                                categoria
                        );
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Em caso de erro na comunicação externa, retorna lista vazia
            return new ArrayList<>();
        }
    }
}