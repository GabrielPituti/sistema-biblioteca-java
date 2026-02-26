package com.desafio.biblioteca.service;

import com.desafio.biblioteca.domain.entity.*;
import com.desafio.biblioteca.domain.repository.EmprestimoRepository;
import com.desafio.biblioteca.domain.repository.LivroRepository;
import com.desafio.biblioteca.dto.EmprestimoRequestDTO;
import com.desafio.biblioteca.dto.EmprestimoResponseDTO;
import com.desafio.biblioteca.dto.LivroResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Serviço de domínio para gestão do ciclo de vida de empréstimos e motor de recomendações.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioService usuarioService;
    private final LivroService livroService;
    private final LivroRepository livroRepository;

    /**
     * Realiza a locação de um livro garantindo a unicidade de empréstimos ativos por obra.
     */
    @Transactional
    public EmprestimoResponseDTO realizarEmprestimo(EmprestimoRequestDTO dto) {
        log.info("Iniciando processo de locação.");

        if (emprestimoRepository.existsByLivroIdAndStatus(dto.livroId(), StatusEmprestimo.ATIVO)) {
            throw new RuntimeException("Este livro já possui um empréstimo ativo.");
        }

        Livro livro = livroService.buscarPorId(dto.livroId());
        Usuario usuario = usuarioService.buscarPorId(dto.usuarioId());

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuario);
        emprestimo.setLivro(livro);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        return EmprestimoResponseDTO.fromEntity(emprestimoRepository.save(emprestimo));
    }

    /**
     * Finaliza um empréstimo ativo e atualiza a data de devolução.
     */
    @Transactional
    public EmprestimoResponseDTO devolverLivro(Long id) {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registo de empréstimo não localizado."));

        if (StatusEmprestimo.DEVOLVIDO.equals(emprestimo.getStatus())) {
            throw new RuntimeException("Este empréstimo já consta como devolvido no sistema.");
        }

        emprestimo.setDataDevolucao(LocalDate.now());
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);

        return EmprestimoResponseDTO.fromEntity(emprestimoRepository.save(emprestimo));
    }

    /**
     * Recomenda obras baseadas nas categorias dos livros já locados pelo usuário.
     */
    @Transactional(readOnly = true)
    public List<LivroResponseDTO> recomendarLivros(Long usuarioId) {
        List<Emprestimo> historico = emprestimoRepository.findByUsuarioId(usuarioId);

        Set<String> categorias = historico.stream()
                .map(e -> e.getLivro().getCategoria())
                .collect(Collectors.toSet());

        if (categorias.isEmpty()) return List.of();

        Set<Long> jaLidos = historico.stream()
                .map(e -> e.getLivro().getId())
                .collect(Collectors.toSet());

        return livroRepository.findByCategoriaInAndIdNotIn(categorias, jaLidos).stream()
                .map(LivroResponseDTO::fromEntity)
                .toList();
    }

    /**
     * Recupera o histórico global de movimentações da biblioteca.
     */
    @Transactional(readOnly = true)
    public List<EmprestimoResponseDTO> listarTodos() {
        return emprestimoRepository.findAll().stream()
                .map(EmprestimoResponseDTO::fromEntity)
                .toList();
    }
}