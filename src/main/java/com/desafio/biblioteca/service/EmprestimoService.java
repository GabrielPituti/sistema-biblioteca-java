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
 * Serviço responsável por orquestrar o ciclo de vida de empréstimos e o motor de recomendações.
 *
 * Esta implementação foca no desacoplamento entre a persistência e a regra de negócio,
 * utilizando processamento em memória via Java Streams para garantir a flexibilidade
 * exigida nos critérios de recomendação por categoria.
 */
@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioService usuarioService;
    private final LivroService livroService;
    private final LivroRepository livroRepository;

    /**
     * Registra um novo empréstimo validando a disponibilidade imediata do exemplar.
     *
     * A restrição de "um empréstimo ativo por vez" é verificada em tempo de execução,
     * garantindo que livros já alocados não possam ser associados a novos usuários
     * até que o status seja alterado para DEVOLVIDO.
     *
     * @param dto Contém as referências de ID para Usuário e Livro.
     * @return Representação do empréstimo persistido.
     * @throws RuntimeException Caso o livro solicitado já possua um empréstimo com status ATIVO.
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
     * Motor de Recomendação Baseado em Categorias do Histórico.
     *
     * O algoritmo analisa as categorias das obras previamente consumidas pelo usuário
     * para sugerir novos títulos do mesmo gênero que ainda não constam em seu histórico
     * de empréstimos, otimizando a descoberta de novos conteúdos.
     *
     * @param usuarioId Identificador único do usuário para cruzamento de dados.
     * @return Coleção de DTOs representando as obras sugeridas.
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