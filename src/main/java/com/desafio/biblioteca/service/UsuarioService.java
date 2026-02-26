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

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Transactional
    public UsuarioResponseDTO salvar(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        atualizarDados(usuario, dto);
        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = buscarPorId(id);
        atualizarDados(usuario, dto);
        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }

    /**
     * Centraliza a lógica de mapeamento de dados do DTO para a Entidade.
     * * Justificativa: Evita a duplicação de lógica entre os métodos de
     * criação e atualização, facilitando a manutenção futura do modelo.
     */
    private void atualizarDados(Usuario usuario, UsuarioRequestDTO dto) {
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setDataCadastro(dto.dataCadastro());
        usuario.setTelefone(dto.telefone());
    }
}