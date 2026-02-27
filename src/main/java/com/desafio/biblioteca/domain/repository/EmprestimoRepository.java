package com.desafio.biblioteca.domain.repository;

import com.desafio.biblioteca.domain.entity.Emprestimo;
import com.desafio.biblioteca.domain.entity.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    boolean existsByLivroIdAndStatus(Long livroId, StatusEmprestimo status);

    List<Emprestimo> findByUsuarioId(Long usuarioId);
}