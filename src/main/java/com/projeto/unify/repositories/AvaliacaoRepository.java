package com.projeto.unify.repositories;

import com.projeto.unify.models.Avaliacao;
import com.projeto.unify.models.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByTurma(Turma turma);
    List<Avaliacao> findByTurmaId(Long turmaId);
    Optional<Avaliacao> findByIdAndTurmaId(Long id, Long turmaId);
    // Adicionar outras consultas específicas se necessário
} 