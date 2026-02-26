package com.desafio.biblioteca.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Representa uma obra literária disponível para empréstimo.
 * * Justificativa: O mapeamento de campos não nulos reflete a obrigatoriedade
 * do modelo de dados. A categoria é mantida como String para permitir a
 * flexibilidade na lógica de recomendação baseada em agrupamentos textuais.
 */
@Entity
@Table(name = "livros")
@Getter
@Setter
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String titulo;

    @NotBlank
    @Column(nullable = false)
    private String autor;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(name = "data_publicacao", nullable = false)
    private LocalDate dataPublicacao;

    @NotBlank
    @Column(nullable = false)
    private String categoria;
}