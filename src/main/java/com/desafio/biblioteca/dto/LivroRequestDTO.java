package com.desafio.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record LivroRequestDTO(
        @NotBlank(message = "O título é obrigatório")
        String titulo,

        @NotBlank(message = "O autor é obrigatório")
        String autor,

        @NotBlank(message = "O ISBN é obrigatório")
        String isbn,

        @NotNull(message = "A data de publicação é obrigatória")
        LocalDate dataPublicacao,

        @NotBlank(message = "A categoria é obrigatória")
        String categoria
) {
}