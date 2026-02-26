package com.desafio.biblioteca.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Captura exceções lançadas em qualquer ponto da aplicação e padroniza a resposta.
 * * Justificativa: Melhora a experiência do desenvolvedor (DX) e do usuário,
 * retornando mensagens claras e códigos HTTP semanticamente corretos,
 * evitando a exposição de stacktraces internos.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata exceções genéricas de lógica de negócio (RuntimeException).
     * Converte o erro 500 (Internal Error) em 400 (Bad Request).
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", 400);

        return ResponseEntity.badRequest().body(body);
    }
}