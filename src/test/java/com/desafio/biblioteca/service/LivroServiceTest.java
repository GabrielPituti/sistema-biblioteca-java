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
 * Suite de testes para manutencao do acervo e integridade dos dados de obras.
 */
@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private LivroService livroService;

    @Test
    @DisplayName("Deve persistir um novo livro quando os dados obrigatorios forem validos")
    void deveSalvarLivroComSucesso() {
        LivroRequestDTO request = new LivroRequestDTO(
                "Clean Architecture", "Robert Martin", "12345", LocalDate.now(), "TI"
        );

        Livro livroSalvo = new Livro();
        livroSalvo.setId(10L);
        livroSalvo.setTitulo(request.titulo());

        when(livroRepository.save(any(Livro.class))).thenReturn(livroSalvo);

        LivroResponseDTO response = livroService.salvar(request);

        assertNotNull(response);
        assertEquals(10L, response.id());
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao tentar recuperar uma obra com identificador inexistente")
    void deveLancarExcecaoQuandoLivroNaoEncontrado() {
        when(livroRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> livroService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve remover permanentemente um livro do acervo local")
    void deveExcluirLivro() {
        Livro livro = new Livro();
        livro.setId(1L);

        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        doNothing().when(livroRepository).delete(livro);

        assertDoesNotThrow(() -> livroService.deletar(1L));
        verify(livroRepository, times(1)).delete(livro);
    }
}