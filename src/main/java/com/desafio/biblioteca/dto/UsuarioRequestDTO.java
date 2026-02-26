package com.desafio.biblioteca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

/**
 * Record para captura de dados na criação/atualização de usuários.
 * * Justificativa: O uso de Record assegura a imutabilidade dos dados de entrada
 * durante o processamento do request. As anotações de validação aqui garantem
 * o "Fail-Fast", interrompendo requisições inválidas antes de atingirem o Service.
 */
public record UsuarioRequestDTO(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotNull @PastOrPresent LocalDate dataCadastro,
        @NotBlank String telefone
) { }