package com.desafio.biblioteca.controller;

import com.desafio.biblioteca.dto.LivroResponseDTO;
import com.desafio.biblioteca.service.GoogleBooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/google-books")
@RequiredArgsConstructor
public class GoogleBooksController {

    private final GoogleBooksService googleBooksService;

    @GetMapping("/search")
    public ResponseEntity<List<LivroResponseDTO>> buscar(@RequestParam String titulo) {
        return ResponseEntity.ok(googleBooksService.buscarLivrosExternos(titulo));
    }
}