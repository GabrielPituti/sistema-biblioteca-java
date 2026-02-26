package com.desafio.biblioteca.domain.repository;

import com.desafio.biblioteca.domain.entity.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    List<Livro> findByCategoriaInAndIdNotIn(Collection<String> categorias, Collection<Long> idsExcluidos);
}