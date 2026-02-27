package com.desafio.biblioteca.dto;

import com.desafio.biblioteca.domain.entity.Usuario;
import java.time.LocalDate;

/**
 * Record de saida para dados do membro.
 */
public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        LocalDate dataCadastro,
        String telefone
) {
    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataCadastro(),
                usuario.getTelefone()
        );
    }
}