package com.desafio.biblioteca.service;

import com.desafio.biblioteca.domain.entity.Livro;
import com.desafio.biblioteca.domain.repository.LivroRepository;
import com.desafio.biblioteca.dto.LivroRequestDTO;
import com.desafio.biblioteca.dto.LivroResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Validação das operações de manutenção do acervo (CRUD de Livros).
 */
@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private LivroService livroService;

    @Test
    @DisplayName("Deve cadastrar um novo livro quando os dados forem válidos")
    void deveSalvarLivroComSucesso() {
        LivroRequestDTO request = new LivroRequestDTO(
                "Design Patterns",
                "Erich Gamma",
                "9780201633610",
                LocalDate.now(),
                "Tecnologia"
        );

        Livro livroSalvo = new Livro();
        livroSalvo.setId(10L);
        livroSalvo.setTitulo(request.titulo());

        when(livroRepository.save(any(Livro.class))).thenReturn(livroSalvo);

        LivroResponseDTO response = livroService.salvar(request);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("Design Patterns", response.titulo());
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve recuperar os detalhes de um livro pelo ID")
    void deveBuscarLivroPorId() {
        Livro livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Refactoring");

        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));

        Livro resultado = livroService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Refactoring", resultado.getTitulo());
    }

    @Test
    @DisplayName("Deve remover um livro do acervo com sucesso")
    void deveExcluirLivro() {
        Livro livro = new Livro();
        livro.setId(1L);

        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        doNothing().when(livroRepository).delete(livro);

        assertDoesNotThrow(() -> livroService.deletar(1L));
        verify(livroRepository, times(1)).delete(livro);
    }
}