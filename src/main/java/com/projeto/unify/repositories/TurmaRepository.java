package com.projeto.unify.repositories;

import com.projeto.unify.models.Turma;
import com.projeto.unify.models.Universidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {

    List<Turma> findByGraduacao_Universidade(Universidade universidade);

    Optional<Turma> findByIdAndGraduacao_Universidade(Long id, Universidade universidade);

    // Add other Turma-specific query methods if needed
}
