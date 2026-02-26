package com.desafio.biblioteca.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Record para solicitação de novo empréstimo.
 */
public record EmprestimoRequestDTO(
        @NotNull Long usuarioId,
        @NotNull Long livroId
) {
}
