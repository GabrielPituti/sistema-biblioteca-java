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
        log.info("Iniciando processo de locação: livro={}, usuario={}", dto.livroId(), dto.usuarioId());

        Livro livro = livroService.buscarPorId(dto.livroId());
        Usuario usuario = usuarioService.buscarPorId(dto.usuarioId());

        // Verificação de disponibilidade com trava de segurança de negócio
        boolean livroJaEmprestado = emprestimoRepository.findAll().stream()
                .anyMatch(e -> e.getLivro().getId().equals(livro.getId())
                        && e.getStatus() == StatusEmprestimo.ATIVO);

        if (livroJaEmprestado) {
            log.warn("Tentativa de empréstimo negada: livro ID {} já possui locação ativa", livro.getId());
            throw new RuntimeException("Este exemplar não está disponível no momento.");
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuario);
        emprestimo.setLivro(livro);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        Emprestimo salvo = emprestimoRepository.save(emprestimo);
        log.info("Empréstimo ID {} registrado com sucesso", salvo.getId());

        return EmprestimoResponseDTO.fromEntity(salvo);
    }

    @Transactional
    public EmprestimoResponseDTO devolverLivro(Long id) {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de empréstimo não localizado."));

        if (StatusEmprestimo.DEVOLVIDO.equals(emprestimo.getStatus())) {
            throw new RuntimeException("Este livro já foi devolvido anteriormente.");
        }

        emprestimo.setDataDevolucao(LocalDate.now());
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);

        log.info("Devolução processada para o empréstimo ID {}", id);
        return EmprestimoResponseDTO.fromEntity(emprestimoRepository.save(emprestimo));
    }

    @Transactional(readOnly = true)
    public List<LivroResponseDTO> recomendarLivros(Long usuarioId) {
        // Lógica de recomendação baseada em afinidade por categoria
        List<Emprestimo> historico = emprestimoRepository.findAll().stream()
                .filter(e -> e.getUsuario().getId().equals(usuarioId))
                .toList();

        Set<String> categoriasLidas = historico.stream()
                .map(e -> e.getLivro().getCategoria())
                .collect(Collectors.toSet());

        Set<Long> idsLidos = historico.stream()
                .map(e -> e.getLivro().getId())
                .collect(Collectors.toSet());

        return livroRepository.findAll().stream()
                .filter(l -> categoriasLidas.contains(l.getCategoria()) && !idsLidos.contains(l.getId()))
                .map(LivroResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmprestimoResponseDTO> listarTodos() {
        return emprestimoRepository.findAll()
                .stream()
                .map(EmprestimoResponseDTO::fromEntity)
                .toList();
    }
}