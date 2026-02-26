package com.desafio.biblioteca.service;

import com.desafio.biblioteca.domain.entity.Usuario;
import com.desafio.biblioteca.domain.repository.UsuarioRepository;
import com.desafio.biblioteca.dto.UsuarioRequestDTO;
import com.desafio.biblioteca.dto.UsuarioResponseDTO;
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
 * Testes de unidade para a gestão de usuários.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve persistir um novo usuário com dados válidos")
    void deveSalvarUsuarioComSucesso() {
        UsuarioRequestDTO request = new UsuarioRequestDTO(
                "Gabriel Pituti",
                "gabriel@email.com",
                LocalDate.now(),
                "11999999999"
        );

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(1L);
        usuarioSalvo.setNome(request.nome());

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        UsuarioResponseDTO response = usuarioService.salvar(request);

        assertNotNull(response);
        assertEquals("Gabriel Pituti", response.nome());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente")
    void deveLancarExcecaoAoBuscarUsuarioInexistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.buscarPorId(1L);
        });

        assertTrue(exception.getMessage().contains("não encontrado"));
    }
}