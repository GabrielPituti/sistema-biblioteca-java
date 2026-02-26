package com.desafio.biblioteca.config;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
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

    /**
     * Trata erros de validação (@Valid).
     * Mapeia cada campo inválido com sua respectiva mensagem de erro definida no DTO.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 400);

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        body.put("errors", errors);

        return ResponseEntity.badRequest().body(body);
    }
}