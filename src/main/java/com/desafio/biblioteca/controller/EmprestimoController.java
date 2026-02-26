package com.desafio.biblioteca.controller;

import com.desafio.biblioteca.dto.EmprestimoRequestDTO;
import com.desafio.biblioteca.dto.EmprestimoResponseDTO;
import com.desafio.biblioteca.dto.LivroResponseDTO;
import com.desafio.biblioteca.service.EmprestimoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Interface REST para operações de movimentação de acervo e inteligência de recomendação.
 */
@CrossOrigin
@RestController
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    /**
     * Endpoint para abertura de novos empréstimos.
     */
    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> criar(@RequestBody @Valid EmprestimoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimoService.realizarEmprestimo(dto));
    }

    /**
     * Endpoint para atualização de empréstimo (Devolução de Livro).
     * * @param id Identificador do empréstimo a ser encerrado.
     * @return Dados do empréstimo com status atualizado.
     */
    @PutMapping("/{id}/devolver")
    public ResponseEntity<EmprestimoResponseDTO> devolver(@PathVariable Long id) {
        return ResponseEntity.ok(emprestimoService.devolverLivro(id));
    }

    /**
     * Recupera sugestões de livros baseadas no perfil de consumo do usuário.
     */
    @GetMapping("/recomendacoes/{usuarioId}")
    public ResponseEntity<List<LivroResponseDTO>> recomendar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(emprestimoService.recomendarLivros(usuarioId));
    }
}