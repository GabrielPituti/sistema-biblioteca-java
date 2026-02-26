package com.desafio.biblioteca.domain.repository;

import com.desafio.biblioteca.domain.entity.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Abstração para operações de banco de dados da entidade Livro.
 * * Utilizado para centralizar a recuperação de obras, servindo de base
 * para a futura implementação do algoritmo de recomendação por categoria.
 */
@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> { }