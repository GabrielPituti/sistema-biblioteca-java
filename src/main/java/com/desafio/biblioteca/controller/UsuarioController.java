package com.desafio.biblioteca.controller;

import com.desafio.biblioteca.dto.UsuarioRequestDTO;
import com.desafio.biblioteca.dto.UsuarioResponseDTO;
import com.desafio.biblioteca.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoint REST para gestão de usuários.
 * * Justificativa: O uso de ResponseEntity permite o controle fino sobre os
 * códigos de status HTTP (201 Created, 200 OK), seguindo as boas práticas
 * de design de APIs RESTful.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@RequestBody @Valid UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.salvar(dto));
    }
}