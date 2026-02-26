package com.desafio.biblioteca.dto;

import com.desafio.biblioteca.domain.entity.Emprestimo;
import com.desafio.biblioteca.domain.entity.StatusEmprestimo;

import java.time.LocalDate;

/**
 * Record para retorno de dados do empréstimo.
 */
public record EmprestimoResponseDTO(
        Long id,
        String nomeUsuario,
        String tituloLivro,
        LocalDate dataEmprestimo,
        LocalDate dataDevolucao,
        StatusEmprestimo status
) {
    public static EmprestimoResponseDTO fromEntity(Emprestimo e) {
        return new EmprestimoResponseDTO(
                e.getId(),
                e.getUsuario().getNome(),
                e.getLivro().getTitulo(),
                e.getDataEmprestimo(),
                e.getDataDevolucao(),
                e.getStatus()
        );
    }
}