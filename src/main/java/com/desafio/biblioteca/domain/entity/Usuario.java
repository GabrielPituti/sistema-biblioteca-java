package com.desafio.biblioteca.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Representa o cliente da biblioteca.
 * * Justificativa: A utilização de validações Bean Validation (Jakarta) garante
 * que a integridade dos dados seja verificada antes da persistência,
 * evitando estados inconsistentes na base de dados conforme solicitado no desafio.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @PastOrPresent
    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;

    @NotBlank
    @Column(nullable = false)
    private String telefone;
}