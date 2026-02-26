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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    @DisplayName("Deve impedir empréstimo quando livro já possui locação ativa no banco")
    void deveLancarExcecaoQuandoLivroJaEstiverOcupado() {
        when(emprestimoRepository.existsByLivroIdAndStatus(1L, StatusEmprestimo.ATIVO)).thenReturn(true);

        EmprestimoRequestDTO request = new EmprestimoRequestDTO(1L, 1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emprestimoService.realizarEmprestimo(request);
        });

        assertEquals("Este livro já possui um empréstimo ativo.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve realizar empréstimo com sucesso quando livro está disponível")
    void deveRealizarEmprestimoComSucesso() {
        when(emprestimoRepository.existsByLivroIdAndStatus(1L, StatusEmprestimo.ATIVO)).thenReturn(false);
        when(livroService.buscarPorId(1L)).thenReturn(livro);
        when(usuarioService.buscarPorId(1L)).thenReturn(usuario);
        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(i -> i.getArguments()[0]);

        EmprestimoRequestDTO request = new EmprestimoRequestDTO(1L, 1L);
        EmprestimoResponseDTO response = emprestimoService.realizarEmprestimo(request);

        assertNotNull(response);
        assertEquals(StatusEmprestimo.ATIVO, response.status());
    }

    @Test
    @DisplayName("Deve registrar a devolução e atualizar o status para DEVOLVIDO")
    void deveDevolverLivroComSucesso() {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setLivro(livro);
        emprestimo.setUsuario(usuario); // ✅ CORREÇÃO AQUI: Evita o NullPointerException
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);

        EmprestimoResponseDTO response = emprestimoService.devolverLivro(1L);

        assertEquals(StatusEmprestimo.DEVOLVIDO, response.status());
        assertNotNull(response.dataDevolucao());
        assertEquals("Gabriel", response.nomeUsuario());
    }

    @Test
    @DisplayName("Deve recomendar livros baseados nas categorias lidas, excluindo os já lidos")
    void deveRecomendarLivrosCorretamente() {
        Emprestimo historico = new Emprestimo();
        historico.setLivro(livro);
        historico.setUsuario(usuario);

        Livro livroSugerido = new Livro();
        livroSugerido.setId(2L);
        livroSugerido.setTitulo("Clean Architecture");
        livroSugerido.setCategoria("Tecnologia");

        when(emprestimoRepository.findByUsuarioId(1L)).thenReturn(List.of(historico));
        when(livroRepository.findByCategoriaInAndIdNotIn(any(), any())).thenReturn(List.of(livroSugerido));

        List<LivroResponseDTO> resultado = emprestimoService.recomendarLivros(1L);

        assertFalse(resultado.isEmpty());
        assertEquals("Clean Architecture", resultado.get(0).titulo());
    }
}