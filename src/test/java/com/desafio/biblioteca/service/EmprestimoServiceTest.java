package com.desafio.biblioteca.service;

import com.desafio.biblioteca.domain.entity.*;
import com.desafio.biblioteca.domain.repository.EmprestimoRepository;
import com.desafio.biblioteca.domain.repository.LivroRepository;
import com.desafio.biblioteca.dto.EmprestimoRequestDTO;
import com.desafio.biblioteca.dto.EmprestimoResponseDTO;
import com.desafio.biblioteca.dto.LivroResponseDTO;
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
 * Suite de testes para validacao das regras de negocio de locacao e recomendacao.
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
        livro.setCategoria("Tecnologia");
    }

    @Test
    @DisplayName("Deve impedir emprestimo quando o livro ja possui uma locacao ativa")
    void deveLancarExcecaoQuandoLivroJaEstiverOcupado() {
        when(emprestimoRepository.existsByLivroIdAndStatus(1L, StatusEmprestimo.ATIVO)).thenReturn(true);

        EmprestimoRequestDTO request = new EmprestimoRequestDTO(1L, 1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emprestimoService.realizarEmprestimo(request));

        assertEquals("Este livro já possui um empréstimo ativo.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve registrar novo emprestimo com sucesso quando o livro estiver disponivel")
    void deveRealizarEmprestimoComSucesso() {
        when(emprestimoRepository.existsByLivroIdAndStatus(1L, StatusEmprestimo.ATIVO)).thenReturn(false);
        when(livroService.buscarPorId(1L)).thenReturn(livro);
        when(usuarioService.buscarPorId(1L)).thenReturn(usuario);
        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(i -> i.getArguments()[0]);

        EmprestimoRequestDTO request = new EmprestimoRequestDTO(1L, 1L);
        EmprestimoResponseDTO response = emprestimoService.realizarEmprestimo(request);

        assertNotNull(response);
        assertEquals(StatusEmprestimo.ATIVO, response.status());
        assertNotNull(response.dataEmprestimo());
    }

    @Test
    @DisplayName("Deve registrar a devolucao com sucesso e atualizar o status para DEVOLVIDO")
    void deveDevolverLivroComSucesso() {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setLivro(livro);
        emprestimo.setUsuario(usuario);
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);

        EmprestimoResponseDTO response = emprestimoService.devolverLivro(1L);

        assertEquals(StatusEmprestimo.DEVOLVIDO, response.status());
        assertNotNull(response.dataDevolucao());
    }

    @Test
    @DisplayName("Deve impedir devolucao de emprestimo que ja foi encerrado")
    void deveLancarExcecaoAoDevolverEmprestimoJaDevolvido() {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setLivro(livro);
        emprestimo.setUsuario(usuario);
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);

        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                emprestimoService.devolverLivro(1L));

        assertTrue(ex.getMessage().contains("já consta como devolvido"));
    }

    @Test
    @DisplayName("Deve lancar excecao ao tentar devolver um emprestimo com identificador inexistente")
    void deveLancarExcecaoAoDevolverEmprestimoInexistente() {
        when(emprestimoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                emprestimoService.devolverLivro(99L));

        assertTrue(ex.getMessage().contains("não localizado"));
    }

    @Test
    @DisplayName("Deve sugerir obras baseadas nas categorias ja consumidas pelo membro")
    void deveRecomendarLivrosCorretamente() {
        Emprestimo historico = new Emprestimo();
        historico.setLivro(livro);
        historico.setUsuario(usuario);

        Livro livroSugerido = new Livro();
        livroSugerido.setId(2L);
        livroSugerido.setTitulo("Refactoring");
        livroSugerido.setCategoria("Tecnologia");

        when(emprestimoRepository.findByUsuarioId(1L)).thenReturn(List.of(historico));
        when(livroRepository.findByCategoriaInAndIdNotIn(anySet(), anySet())).thenReturn(List.of(livroSugerido));

        List<LivroResponseDTO> resultado = emprestimoService.recomendarLivros(1L);

        assertFalse(resultado.isEmpty());
        assertEquals("Refactoring", resultado.get(0).titulo());
    }
}