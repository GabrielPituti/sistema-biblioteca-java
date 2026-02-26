package com.desafio.biblioteca.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Mapeamento da resposta da Google Books API.
 * * Este Record utiliza aninhamento para representar a estrutura hierárquica do JSON
 * retornado pelo Google, mantendo o encapsulamento dentro de um único arquivo
 * para facilitar a manutenção da integração externa.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleBookDTO(
        List<Item> items
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            VolumeInfo volumeInfo
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VolumeInfo(
            String title,
            List<String> authors,
            String publisher,
            String publishedDate,
            String description,
            List<IndustryIdentifier> industryIdentifiers,
            List<String> categories // Corrigido para List<String> para bater com a API do Google
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record IndustryIdentifier(
            String type,
            String identifier
    ) {}
}