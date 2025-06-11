package com.projeto.unify.repositories;

import com.projeto.unify.models.Professor;
import com.projeto.unify.models.Turma;
import com.projeto.unify.models.Universidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {

    List<Turma> findByProfessor_Universidade(Universidade universidade);

    @Query("SELECT t FROM Turma t JOIN FETCH t.materia m JOIN FETCH t.professor p LEFT JOIN FETCH t.alunos a WHERE p.universidade.id = :universidadeId")
    List<Turma> findAllByProfessor_Universidade_Id(@Param("universidadeId") Long universidadeId);

    @Query("SELECT t FROM Turma t JOIN FETCH t.materia m JOIN FETCH t.professor p LEFT JOIN FETCH t.alunos a WHERE t.id = :id AND p.universidade = :universidade")
    Optional<Turma> findByIdAndProfessor_Universidade(@Param("id") Long id, @Param("universidade") Universidade universidade);

    boolean existsByProfessorAndTurno(Professor professor, String turno);

    boolean existsByProfessorAndTurnoAndDiaSemana(Professor professor, String turno, String diaSemana);

    @Query("SELECT t FROM Turma t JOIN t.alunos a WHERE a.id = :alunoId")
    List<Turma> findAllByAlunos_Id(@Param("alunoId") Long alunoId);

    @Query("SELECT t FROM Turma t JOIN FETCH t.materia JOIN FETCH t.professor JOIN t.alunos a WHERE a.id = :alunoId")
    List<Turma> findAllByAlunoIdWithDetails(@Param("alunoId") Long alunoId);

    // Add other Turma-specific query methods if needed
}
