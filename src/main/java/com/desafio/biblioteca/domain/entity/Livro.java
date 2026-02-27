package com.desafio.biblioteca.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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