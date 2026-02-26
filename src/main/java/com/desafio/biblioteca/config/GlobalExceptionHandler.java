package com.desafio.biblioteca.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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
 * Centralizador de exceções da camada de apresentação.
 * * Justificativa: Padroniza o contrato de erro da API, garantindo que o Frontend
 * receba sempre a mesma estrutura de resposta. Separa erros de negócio (400/409)
 * de falhas inesperadas de infraestrutura (500), elevando a maturidade do sistema.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata violações de integridade do banco de dados (ex: Unique Constraints).
     * Retorna 409 Conflict.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrity(DataIntegrityViolationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("message", "Conflito de integridade: A operação viola uma regra de unicidade ou consistência no banco de dados.");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Trata exceções de lógica de negócio lançadas manualmente.
     * Retorna 400 Bad Request.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Captura erros de validação de campos (@Valid).
     * Mapeia cada atributo inválido com sua respectiva mensagem amigável.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        body.put("errors", errors);

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handler "Catch-All" para exceções genéricas inesperadas.
     * Garante que o cliente receba um 500 amigável em vez de um stacktrace técnico.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("message", "Ocorreu um erro interno inesperado. Por favor, tente novamente mais tarde.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}