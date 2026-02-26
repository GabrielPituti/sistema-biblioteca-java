package com.desafio.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Record para entrada de dados de Livros.
 */
public record LivroRequestDTO(
        @NotBlank String titulo,
        @NotBlank String autor,
        @NotBlank String isbn,
        @NotNull LocalDate dataPublicacao,
        @NotBlank String categoria
) {
}
