package com.desafio.biblioteca.service;

import com.desafio.biblioteca.domain.entity.Livro;
import com.desafio.biblioteca.domain.repository.LivroRepository;
import com.desafio.biblioteca.dto.LivroRequestDTO;
import com.desafio.biblioteca.dto.LivroResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Camada de servico para manutencao do acervo e regras de persistencia de obras.
 */
@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    @Transactional(readOnly = true)
    public List<LivroResponseDTO> listarTodos() {
        return livroRepository.findAll().stream()
                .map(LivroResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public LivroResponseDTO salvar(LivroRequestDTO dto) {
        Livro livro = new Livro();
        atualizarDados(livro, dto);
        return LivroResponseDTO.fromEntity(livroRepository.save(livro));
    }

    @Transactional(readOnly = true)
    public Livro buscarPorId(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obra nao localizada no acervo"));
    }

    @Transactional
    public LivroResponseDTO atualizar(Long id, LivroRequestDTO dto) {
        Livro livro = buscarPorId(id);
        atualizarDados(livro, dto);
        return LivroResponseDTO.fromEntity(livroRepository.save(livro));
    }

    @Transactional
    public void deletar(Long id) {
        Livro livro = buscarPorId(id);
        livroRepository.delete(livro);
    }

    private void atualizarDados(Livro livro, LivroRequestDTO dto) {
        livro.setTitulo(dto.titulo());
        livro.setAutor(dto.autor());
        livro.setIsbn(dto.isbn());
        livro.setDataPublicacao(dto.dataPublicacao());
        livro.setCategoria(dto.categoria());
    }
}