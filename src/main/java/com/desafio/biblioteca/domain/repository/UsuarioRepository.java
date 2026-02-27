package com.desafio.biblioteca.domain.repository;

import com.desafio.biblioteca.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Operacoes de persistencia para a entidade Usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}