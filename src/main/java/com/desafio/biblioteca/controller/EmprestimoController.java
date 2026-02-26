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
 * Interface de entrada para operações de empréstimo e recomendações.
 * * Justificativa: Centraliza as operações de movimentação do acervo.
 * O endpoint de recomendações utiliza o ID do usuário para fornecer
 * sugestões personalizadas, agregando valor à experiência do cliente.
 */
@RestController
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> criar(@RequestBody @Valid EmprestimoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimoService.realizarEmprestimo(dto));
    }

    @GetMapping("/recomendacoes/{usuarioId}")
    public ResponseEntity<List<LivroResponseDTO>> recomendar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(emprestimoService.recomendarLivros(usuarioId));
    }
}