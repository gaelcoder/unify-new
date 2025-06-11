package com.projeto.unify.repositories;

import com.projeto.unify.models.Aluno;
import com.projeto.unify.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.projeto.unify.models.Universidade;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByTelefone(String telefone);
    Optional<Aluno> findByCpf(String cpf);
    Optional<Aluno> findByEmail(String email);
    Optional<Aluno> findByTelefone(String telefone);
    Optional<Aluno> findByUsuario(Usuario usuario);


    @Query("SELECT COUNT(a) FROM Aluno a WHERE a.universidade = :universidade AND a.matricula LIKE CONCAT(:prefixoMatricula, '%')")
    long countByUniversidadeAndMatriculaStartingWith(@Param("universidade") Universidade universidade, @Param("prefixoMatricula") String prefixoMatricula);

    List<Aluno> findByUniversidade(Universidade universidade);
    Optional<Aluno> findByIdAndUniversidade(Long id, Universidade universidade);
    long countByUniversidadeId(Long universidadeId);

    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByCpfAndIdNot(String cpf, Long id);

    @Query("SELECT a FROM Aluno a JOIN a.graduacao g JOIN g.materias m " +
           "WHERE a.universidade.id = :universidadeId " +
           "AND a.campus = :campus " +
           "AND m.id = :materiaId " +
           "AND ( " +
           "  NOT EXISTS (SELECT t FROM Turma t JOIN t.alunos al WHERE al.id = a.id AND t.diaSemana = :diaSemana AND t.turno = :turno) " +
           "  OR a.id IN (SELECT al_edit.id FROM Turma t_edit JOIN t_edit.alunos al_edit WHERE t_edit.id = :turmaId) " +
           ")")
    List<Aluno> findAlunosElegiveisParaTurmaComEdicao(@Param("universidadeId") Long universidadeId,
                                                     @Param("campus") String campus,
                                                     @Param("materiaId") Long materiaId,
                                                     @Param("diaSemana") String diaSemana,
                                                     @Param("turno") String turno,
                                                     @Param("turmaId") Long turmaId);

    @Query("SELECT a FROM Aluno a JOIN a.graduacao g JOIN g.materias m " +
           "WHERE a.universidade.id = :universidadeId " +
           "AND a.campus = :campus " +
           "AND m.id = :materiaId " +
           "AND NOT EXISTS (" +
           "  SELECT t FROM Turma t JOIN t.alunos al " +
           "  WHERE al.id = a.id " +
           "  AND t.diaSemana = :diaSemana " +
           "  AND t.turno = :turno" +
           ")")
    List<Aluno> findAlunosElegiveisParaTurma(@Param("universidadeId") Long universidadeId,
                                            @Param("campus") String campus,
                                            @Param("materiaId") Long materiaId,
                                            @Param("diaSemana") String diaSemana,
                                            @Param("turno") String turno);

    @Query("SELECT a FROM Aluno a JOIN FETCH a.graduacao WHERE a.universidade = :universidade")
    List<Aluno> findByUniversidadeWithGraduacao(@Param("universidade") Universidade universidade);
} 