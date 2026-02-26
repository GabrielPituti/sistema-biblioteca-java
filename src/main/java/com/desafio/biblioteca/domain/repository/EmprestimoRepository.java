package com.desafio.biblioteca.domain.repository;

import com.desafio.biblioteca.domain.entity.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Abstração para operações de banco de dados da entidade Emprestimo.
 * * Essencial para o controle de inventário e validação de disponibilidade
 * de títulos, permitindo rastrear o histórico de locações de cada usuário.
 */
@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> { }