package com.desafio.biblioteca.controller;

import com.desafio.biblioteca.dto.LivroResponseDTO;
import com.desafio.biblioteca.service.GoogleBooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para integração com a Google Books API (Funcionalidade Bônus).
 * * Permite a busca de obras em fontes externas para auxiliar no
 * preenchimento do acervo local.
 */
@CrossOrigin
@RestController
@RequestMapping("/api/google-books")
@RequiredArgsConstructor
public class GoogleBooksController {

    private final GoogleBooksService googleBooksService;

    /**
     * Endpoint de busca por título na API externa.
     * * @param titulo Título ou palavra-chave para pesquisa.
     * @return Lista de livros sugeridos pela API do Google.
     */
    @GetMapping("/search")
    public ResponseEntity<List<LivroResponseDTO>> buscar(@RequestParam String titulo) {
        return ResponseEntity.ok(googleBooksService.buscarLivrosExternos(titulo));
    }
}