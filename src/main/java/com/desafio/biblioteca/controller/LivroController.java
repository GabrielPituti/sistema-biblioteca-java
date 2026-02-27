package com.desafio.biblioteca.controller;

import com.desafio.biblioteca.dto.LivroRequestDTO;
import com.desafio.biblioteca.dto.LivroResponseDTO;
import com.desafio.biblioteca.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoint de gestao do acervo local da biblioteca.
 */
@CrossOrigin
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
    public ResponseEntity<LivroResponseDTO> salvar(@RequestBody @Valid LivroRequestDTO dto) {
        return ResponseEntity.status(201).body(livroService.salvar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(LivroResponseDTO.fromEntity(livroService.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid LivroRequestDTO dto) {
        return ResponseEntity.ok(livroService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        livroService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}