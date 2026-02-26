package com.desafio.biblioteca.domain.entity;

/**
 * Define os estados de ciclo de vida de um empréstimo.
 * * Justificativa: O uso de Enum previne o uso de "strings mágicas" no código,
 * facilitando a implementação da regra de negócio que impede empréstimos
 * simultâneos de um mesmo livro se houver um registro ATIVO.
 */
public enum StatusEmprestimo {
    ATIVO,
    DEVOLVIDO
}