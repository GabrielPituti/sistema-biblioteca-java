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
 * Serviço responsável pela gestão do acervo literário da biblioteca.
 *
 * Esta classe provê as operações de CRUD para a entidade Livro, utilizando
 * DTOs para assegurar o encapsulamento do modelo de domínio e Java Streams
 * para conversão eficiente de coleções.
 */
@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    /**
     * Recupera todos os livros cadastrados no sistema.
     *
     * @return Lista de objetos de transferência contendo os dados das obras.
     */
    @Transactional(readOnly = true)
    public List<LivroResponseDTO> listarTodos() {
        return livroRepository.findAll().stream()
                .map(LivroResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Persiste uma nova obra no acervo.
     *
     * @param dto Objeto contendo os dados necessários para o cadastro.
     * @return DTO representando o livro persistido com seu identificador único.
     */
    @Transactional
    public LivroResponseDTO salvar(LivroRequestDTO dto) {
        Livro livro = new Livro();
        atualizarDados(livro, dto);
        return LivroResponseDTO.fromEntity(livroRepository.save(livro));
    }

    /**
     * Busca uma obra específica através do seu identificador.
     *
     * @param id Identificador único do livro.
     * @return Instância da entidade Livro.
     * @throws RuntimeException Caso o identificador não corresponda a nenhum registro.
     */
    @Transactional(readOnly = true)
    public Livro buscarPorId(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
    }

    /**
     * Atualiza os dados de uma obra existente.
     *
     * @param id Identificador do livro a ser modificado.
     * @param dto Novos dados para atualização.
     * @return DTO com as informações atualizadas.
     */
    @Transactional
    public LivroResponseDTO atualizar(Long id, LivroRequestDTO dto) {
        Livro livro = buscarPorId(id);
        atualizarDados(livro, dto);
        return LivroResponseDTO.fromEntity(livroRepository.save(livro));
    }

    /**
     * Remove uma obra do acervo.
     *
     * @param id Identificador do livro a ser excluído.
     */
    @Transactional
    public void deletar(Long id) {
        Livro livro = buscarPorId(id);
        livroRepository.delete(livro);
    }

    /**
     * Realiza o mapeamento de atributos do DTO para a entidade.
     *
     * @param livro Entidade de destino.
     * @param dto DTO de origem.
     */
    private void atualizarDados(Livro livro, LivroRequestDTO dto) {
        livro.setTitulo(dto.titulo());
        livro.setAutor(dto.autor());
        livro.setIsbn(dto.isbn());
        livro.setDataPublicacao(dto.dataPublicacao());
        livro.setCategoria(dto.categoria());
    }
}