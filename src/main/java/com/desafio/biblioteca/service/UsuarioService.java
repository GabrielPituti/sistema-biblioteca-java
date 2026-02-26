package com.desafio.biblioteca.service;

import com.desafio.biblioteca.domain.entity.Usuario;
import com.desafio.biblioteca.domain.repository.UsuarioRepository;
import com.desafio.biblioteca.dto.UsuarioRequestDTO;
import com.desafio.biblioteca.dto.UsuarioResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Gerencia as regras de negócio para Usuários.
 * * Justificativa: O uso de @Transactional assegura a atomicidade das operações.
 * A utilização de Java Streams para conversão de entidades em DTOs mantém o
 * código declarativo e alinhado com as práticas modernas do Java 21.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioResponseDTO salvar(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setDataCadastro(dto.dataCadastro());
        usuario.setTelefone(dto.telefone());

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    // Futuros métodos de Update e Delete aqui
}