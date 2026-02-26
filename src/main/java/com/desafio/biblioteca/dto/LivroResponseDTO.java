package com.desafio.biblioteca.dto;

import com.desafio.biblioteca.domain.entity.Livro;

import java.time.LocalDate;

/**
 * Record para saída de dados de Livros.
 */
public record LivroResponseDTO(
        Long id,
        String titulo,
        String autor,
        String isbn,
        LocalDate dataPublicacao,
        String categoria
) {
    public static LivroResponseDTO fromEntity(Livro livro) {
        return new LivroResponseDTO(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getDataPublicacao(),
                livro.getCategoria()
        );
    }
}