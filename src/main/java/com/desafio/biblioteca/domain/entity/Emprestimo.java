package com.desafio.biblioteca.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Gerencia o vínculo entre um usuário e um livro por um período determinado.
 * * Justificativa: O uso de FetchType.LAZY nas associações Many-to-One evita o
 * carregamento desnecessário de objetos pesados do banco, otimizando a
 * performance de consultas que listam apenas os dados básicos do empréstimo.
 */
@Entity
@Table(name = "emprestimos")
@Getter
@Setter
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @NotNull
    @PastOrPresent
    @Column(name = "data_emprestimo", nullable = false)
    private LocalDate dataEmprestimo;

    @Column(name = "data_devolucao")
    private LocalDate dataDevolucao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEmprestimo status;
}