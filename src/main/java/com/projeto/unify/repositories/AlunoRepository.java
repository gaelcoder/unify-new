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

    // Methods for FuncionarioSecretaria CRUD
    List<Aluno> findByUniversidade(Universidade universidade);
    Optional<Aluno> findByIdAndUniversidade(Long id, Universidade universidade);
    long countByUniversidadeId(Long universidadeId);

    // Methods for conflict checking during updates
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByCpfAndIdNot(String cpf, Long id);

    @Query(value = "SELECT a.* FROM aluno a " +
           "INNER JOIN materia_graduacao mg ON a.graduacao_id = mg.graduacao_id " +
           "WHERE a.universidade_id = :universidadeId " +
           "AND a.campus = :campus " +
           "AND mg.materia_id = :materiaId", nativeQuery = true)
    List<Aluno> findAlunosElegiveisParaTurma(@Param("universidadeId") Long universidadeId,
                                            @Param("campus") String campus,
                                            @Param("materiaId") Long materiaId);

    @Query("SELECT a FROM Aluno a JOIN FETCH a.graduacao WHERE a.universidade = :universidade")
    List<Aluno> findByUniversidadeWithGraduacao(@Param("universidade") Universidade universidade);
} 