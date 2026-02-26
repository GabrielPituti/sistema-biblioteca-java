package com.desafio.biblioteca.domain.repository;

import com.desafio.biblioteca.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Abstração para operações de banco de dados da entidade Usuario.
 * * A escolha do JpaRepository permite o acesso a métodos de persistência
 * padronizados e consultas derivadas por nome de método, reduzindo a
 * complexidade de escrita de SQL manual para operações comuns.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> { }