package com.desafio.biblioteca.service;

import com.desafio.biblioteca.domain.entity.*;
import com.desafio.biblioteca.domain.repository.EmprestimoRepository;
import com.desafio.biblioteca.domain.repository.LivroRepository;
import com.desafio.biblioteca.dto.EmprestimoRequestDTO;
import com.desafio.biblioteca.dto.EmprestimoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes de unidade para as regras de negócio de empréstimos.
 * * Esta classe valida o fluxo de locação, as travas de segurança de livros ocupados
 * e o processo de devolução, garantindo a integridade do acervo.
 */
@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock private EmprestimoRepository emprestimoRepository;
    @Mock private UsuarioService usuarioService;
    @Mock private LivroService livroService;
    @Mock private LivroRepository livroRepository;

    @InjectMocks
    private EmprestimoService emprestimoService;

    private Usuario usuario;
    private Livro livro;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Gabriel");

        livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Clean Code");
    }

    @Test
    @DisplayName("Deve impedir empréstimo de livro que já possui locação ativa")
    void deveLancarExcecaoQuandoLivroJaEstiverOcupado() {
        Emprestimo emprestimoAtivo = new Emprestimo();
        emprestimoAtivo.setLivro(livro);
        emprestimoAtivo.setStatus(StatusEmprestimo.ATIVO);

        when(livroService.buscarPorId(1L)).thenReturn(livro);
        when(usuarioService.buscarPorId(1L)).thenReturn(usuario);
        when(emprestimoRepository.findAll()).thenReturn(List.of(emprestimoAtivo));

        EmprestimoRequestDTO request = new EmprestimoRequestDTO(1L, 1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emprestimoService.realizarEmprestimo(request);
        });

        assertEquals("Este livro já possui um empréstimo ativo.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve permitir empréstimo quando não houver locações ativas para o livro")
    void deveRealizarEmprestimoComSucesso() {
        when(livroService.buscarPorId(1L)).thenReturn(livro);
        when(usuarioService.buscarPorId(1L)).thenReturn(usuario);
        when(emprestimoRepository.findAll()).thenReturn(List.of());
        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(i -> i.getArguments()[0]);

        EmprestimoRequestDTO request = new EmprestimoRequestDTO(1L, 1L);
        EmprestimoResponseDTO response = emprestimoService.realizarEmprestimo(request);

        assertNotNull(response);
        assertEquals(StatusEmprestimo.ATIVO, response.status());
    }

    @Test
    @DisplayName("Deve registrar a devolução de um livro com sucesso")
    void deveDevolverLivroComSucesso() {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setUsuario(usuario);
        emprestimo.setLivro(livro);
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);

        EmprestimoResponseDTO response = emprestimoService.devolverLivro(1L);

        assertEquals(StatusEmprestimo.DEVOLVIDO, response.status());
        assertNotNull(response.dataDevolucao());
        verify(emprestimoRepository, times(1)).save(emprestimo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar devolver um livro que já foi devolvido")
    void deveLancarExcecaoAoDevolverLivroJaDevolvido() {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);

        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emprestimoService.devolverLivro(1L);
        });

        assertEquals("Este empréstimo já consta como devolvido no sistema.", exception.getMessage());
    }
}