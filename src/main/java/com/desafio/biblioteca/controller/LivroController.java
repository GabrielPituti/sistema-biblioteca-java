package com.desafio.biblioteca.controller;

import com.desafio.biblioteca.dto.LivroRequestDTO;
import com.desafio.biblioteca.dto.LivroResponseDTO;
import com.desafio.biblioteca.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoint REST para gestão do acervo.
 * * Justificativa: A anotação @Valid garante que as restrições impostas no
 * LivroRequestDTO sejam validadas antes da lógica de negócio ser executada,
 * retornando 400 Bad Request automaticamente em caso de erro.
 */
@RestController
@RequestMapping("/api/livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService livroService;

    @GetMapping
    public ResponseEntity<List<LivroResponseDTO>> listar() {
        return ResponseEntity.ok(livroService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<LivroResponseDTO> criar(@RequestBody @Valid LivroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(livroService.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid LivroRequestDTO dto) {
        return ResponseEntity.ok(livroService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        livroService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}