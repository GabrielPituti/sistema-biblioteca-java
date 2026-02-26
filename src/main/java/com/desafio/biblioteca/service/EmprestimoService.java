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

@Slf4j
@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioService usuarioService;
    private final LivroService livroService;
    private final LivroRepository livroRepository;

    @Transactional
    public EmprestimoResponseDTO realizarEmprestimo(EmprestimoRequestDTO dto) {
        log.info("Processando novo empréstimo. Livro: {}, Membro: {}", dto.livroId(), dto.usuarioId());

        // Validação de disponibilidade via query otimizada (evita Race Conditions)
        if (emprestimoRepository.existsByLivroIdAndStatus(dto.livroId(), StatusEmprestimo.ATIVO)) {
            log.warn("Tentativa de empréstimo inválida: Livro {} já está alocado", dto.livroId());
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

    @Transactional
    public EmprestimoResponseDTO devolverLivro(Long id) {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registo de empréstimo não localizado."));

        if (StatusEmprestimo.DEVOLVIDO.equals(emprestimo.getStatus())) {
            throw new RuntimeException("Este empréstimo já consta como devolvido no sistema.");
        }

        emprestimo.setDataDevolucao(LocalDate.now());
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);

        log.info("Devolução concluída para a obra: {}", emprestimo.getLivro().getTitulo());
        return EmprestimoResponseDTO.fromEntity(emprestimoRepository.save(emprestimo));
    }

    @Transactional(readOnly = true)
    public List<LivroResponseDTO> recomendarLivros(Long usuarioId) {
        // Busca histórica otimizada
        List<Emprestimo> historico = emprestimoRepository.findByUsuarioId(usuarioId);

        Set<String> categorias = historico.stream()
                .map(e -> e.getLivro().getCategoria())
                .collect(Collectors.toSet());

        if (categorias.isEmpty()) {
            return List.of();
        }

        Set<Long> jaLidos = historico.stream()
                .map(e -> e.getLivro().getId())
                .collect(Collectors.toSet());

        return livroRepository.findByCategoriaInAndIdNotIn(categorias, jaLidos).stream()
                .map(LivroResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmprestimoResponseDTO> listarTodos() {
        return emprestimoRepository.findAll().stream()
                .map(EmprestimoResponseDTO::fromEntity)
                .toList();
    }
}