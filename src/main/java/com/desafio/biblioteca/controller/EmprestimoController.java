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

@CrossOrigin
@RestController
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @GetMapping
    public ResponseEntity<List<EmprestimoResponseDTO>> listar() {
        return ResponseEntity.ok(emprestimoService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> criar(@RequestBody @Valid EmprestimoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimoService.realizarEmprestimo(dto));
    }

    @PutMapping("/{id}/devolver")
    public ResponseEntity<EmprestimoResponseDTO> devolver(@PathVariable Long id) {
        return ResponseEntity.ok(emprestimoService.devolverLivro(id));
    }

    @GetMapping("/recomendacoes/{usuarioId}")
    public ResponseEntity<List<LivroResponseDTO>> recomendar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(emprestimoService.recomendarLivros(usuarioId));
    }
}