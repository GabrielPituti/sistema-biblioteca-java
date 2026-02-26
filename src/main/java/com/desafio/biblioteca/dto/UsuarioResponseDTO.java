package com.desafio.biblioteca.dto;

import com.desafio.biblioteca.domain.entity.Usuario;
import java.time.LocalDate;

/**
 * Record para envio de dados do usuário para o cliente da API.
 * * Justificativa: Este DTO isola a entidade JPA da camada de visualização,
 * permitindo que alteremos a estrutura da base de dados no futuro sem
 * quebrar o contrato com o Frontend ou expor campos sensíveis.
 */
public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        LocalDate dataCadastro,
        String telefone
) {
    /**
     * Mapper manual para converter Entidade em DTO.
     * * Justificativa: Evita a dependência de bibliotecas externas de mapping
     * em desafios pequenos, mantendo o controle total sobre a transformação.
     */
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