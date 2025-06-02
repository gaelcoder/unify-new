package com.projeto.unify.repositories;

import com.projeto.unify.models.Aluno;
import com.projeto.unify.models.Avaliacao;
import com.projeto.unify.models.Nota;
import com.projeto.unify.models.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByAluno(Aluno aluno);
    List<Nota> findByAvaliacao(Avaliacao avaliacao);
    Optional<Nota> findByAlunoAndAvaliacao(Aluno aluno, Avaliacao avaliacao);
    List<Nota> findByAlunoIdAndAvaliacaoId(Long alunoId, Long avaliacaoId);
    List<Nota> findByAvaliacaoTurma(Turma turma);
    List<Nota> findByAlunoIdAndAvaliacaoTurmaId(Long alunoId, Long turmaId);

    // Para calcular a média de um aluno em uma turma específica
    @Query("SELECT AVG(n.valorObtido) FROM Nota n WHERE n.aluno.id = :alunoId AND n.avaliacao.turma.id = :turmaId")
    Double findAverageNotaByAlunoAndTurma(@Param("alunoId") Long alunoId, @Param("turmaId") Long turmaId);

    // Para buscar todas as notas de um aluno em todas as avaliações de uma turma
    @Query("SELECT n FROM Nota n JOIN n.avaliacao a WHERE n.aluno.id = :alunoId AND a.turma.id = :turmaId")
    List<Nota> findAllByAlunoIdAndTurmaId(@Param("alunoId") Long alunoId, @Param("turmaId") Long turmaId);
} 