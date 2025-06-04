package com.projeto.unify.repositories;

import com.projeto.unify.models.Professor;
import com.projeto.unify.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
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

    // Methods for conflict checking during updates
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByCpfAndIdNot(String cpf, Long id);
}