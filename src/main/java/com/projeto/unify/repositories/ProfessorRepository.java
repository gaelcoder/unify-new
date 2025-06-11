package com.projeto.unify.repositories;

import com.projeto.unify.models.Professor;
import com.projeto.unify.models.Universidade;
import com.projeto.unify.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByTelefone(String telefone);
    Optional<Professor> findByCpf(String cpf);
    Optional<Professor> findByEmail(String email);
    Optional<Professor> findByTelefone(String telefone);
    Optional<Professor> findByUsuario(Usuario usuario);
    Optional<Professor> findByUsuarioId(Long usuarioId);
    List<Professor> findByUniversidadeId(Long universidadeId);
    Optional<Professor> findByIdAndUniversidade(Long id, com.projeto.unify.models.Universidade universidade);
    List<Professor> findByUniversidade(com.projeto.unify.models.Universidade universidade);

    @Query("SELECT p FROM Professor p WHERE p.universidade.id = :universidadeId AND p.id NOT IN " +
           "(SELECT t.professor.id FROM Turma t WHERE t.diaSemana = :diaSemana AND t.turno = :turno)")
    List<Professor> findProfessoresDisponiveis(@Param("universidadeId") Long universidadeId,
                                               @Param("diaSemana") String diaSemana,
                                               @Param("turno") String turno);


    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByCpfAndIdNot(String cpf, Long id);
    boolean existsByTelefoneAndIdNot(String telefone, Long id);
}