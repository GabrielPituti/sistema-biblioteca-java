package com.desafio.biblioteca.service;

import com.desafio.biblioteca.domain.entity.*;
import com.desafio.biblioteca.domain.repository.EmprestimoRepository;
import com.desafio.biblioteca.domain.repository.LivroRepository;
import com.desafio.biblioteca.dto.EmprestimoRequestDTO;
import com.desafio.biblioteca.dto.EmprestimoResponseDTO;
import com.desafio.biblioteca.dto.LivroResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Serviço responsável por gerenciar o ciclo de vida de empréstimos e o motor de recomendações.
 *
 * Esta implementação centraliza as validações de disponibilidade de acervo e utiliza
 * Java Streams para processamento em memória de categorias e filtragem de sugestões,
 * garantindo desacoplamento entre as regras de negócio e a persistência.
 */
@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioService usuarioService;
    private final LivroService livroService;
    private final LivroRepository livroRepository;

    /**
     * Registra um novo empréstimo no sistema após validar a disponibilidade da obra.
     *
     * A validação de disponibilidade é feita verificando a existência de empréstimos
     * com status ATIVO para o livro solicitado, impedindo duplicidade de locação.
     *
     * @param dto Dados de entrada contendo IDs de usuário e livro.
     * @return Dados do empréstimo persistido.
     * @throws RuntimeException Caso o livro já esteja emprestado.
     */
    @Transactional
    public EmprestimoResponseDTO realizarEmprestimo(EmprestimoRequestDTO dto) {
        Livro livro = livroService.buscarPorId(dto.livroId());
        Usuario usuario = usuarioService.buscarPorId(dto.usuarioId());

        boolean livroOcupado = emprestimoRepository.findAll().stream()
                .anyMatch(e -> e.getLivro().getId().equals(livro.getId())
                        && e.getStatus() == StatusEmprestimo.ATIVO);

        if (livroOcupado) {
            throw new RuntimeException("Este livro já possui um empréstimo ativo.");
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuario);
        emprestimo.setLivro(livro);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        return EmprestimoResponseDTO.fromEntity(emprestimoRepository.save(emprestimo));
    }

    /**
     * Motor de Recomendação Baseado em Histórico de Categorias.
     *
     * O algoritmo identifica as categorias de interesse do usuário através de seu
     * histórico de empréstimos e sugere novas obras do acervo que pertençam a estas
     * categorias e que ainda não tenham sido lidas pelo usuário.
     *
     * @param usuarioId Identificador único do usuário.
     * @return Lista de livros recomendados convertidos em DTO.
     */
    @Transactional(readOnly = true)
    public List<LivroResponseDTO> recomendarLivros(Long usuarioId) {
        List<Emprestimo> historico = emprestimoRepository.findAll().stream()
                .filter(e -> e.getUsuario().getId().equals(usuarioId))
                .toList();

        Set<String> categoriasInteresse = historico.stream()
                .map(e -> e.getLivro().getCategoria())
                .collect(Collectors.toSet());

        Set<Long> livrosJaLidos = historico.stream()
                .map(e -> e.getLivro().getId())
                .collect(Collectors.toSet());

        return livroRepository.findAll().stream()
                .filter(l -> categoriasInteresse.contains(l.getCategoria()))
                .filter(l -> !livrosJaLidos.contains(l.getId()))
                .map(LivroResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}