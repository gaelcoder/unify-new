package com.projeto.unify.repositories;

import com.projeto.unify.models.Graduacao;
import com.projeto.unify.models.Materia;
import com.projeto.unify.models.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {

    // Find turmas by the university ID through the Graduacao association
    @Query("SELECT t FROM Turma t WHERE t.graduacao.universidade.id = :universidadeId")
    List<Turma> findByGraduacaoUniversidadeId(@Param("universidadeId") Long universidadeId);

    // Check if a Turma exists for a given Materia, Graduacao, Turno, and UniversidadeId
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END " +
           "FROM Turma t WHERE t.materia = :materia AND t.graduacao = :graduacao AND t.turno = :turno AND t.graduacao.universidade.id = :universidadeId")
    boolean existsByMateriaAndGraduacaoAndTurnoAndUniversidadeId(
            @Param("materia") Materia materia,
            @Param("graduacao") Graduacao graduacao,
            @Param("turno") String turno,
            @Param("universidadeId") Long universidadeId);

    // Check if another Turma exists with the same Materia, Graduacao, Turno, and UniversidadeId, excluding a specific Turma ID
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END " +
           "FROM Turma t WHERE t.materia = :materia AND t.graduacao = :graduacao AND t.turno = :turno AND t.graduacao.universidade.id = :universidadeId AND t.id <> :turmaId")
    boolean existsByMateriaAndGraduacaoAndTurnoAndUniversidadeIdAndIdNot(
            @Param("materia") Materia materia,
            @Param("graduacao") Graduacao graduacao,
            @Param("turno") String turno,
            @Param("universidadeId") Long universidadeId,
            @Param("turmaId") Long turmaId);

    List<Turma> findByMateriaId(Long materiaId);
    List<Turma> findByGraduacaoId(Long graduacaoId);

}
