package com.desafio.biblioteca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public record UsuarioRequestDTO(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotNull @PastOrPresent LocalDate dataCadastro,
        @NotBlank String telefone
) { }