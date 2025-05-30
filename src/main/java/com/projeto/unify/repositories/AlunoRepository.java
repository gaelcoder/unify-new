package com.projeto.unify.repositories;

import com.projeto.unify.models.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByTelefone(String telefone);
    Optional<Aluno> findByCpf(String cpf);
    Optional<Aluno> findByEmail(String email);
    Optional<Aluno> findByTelefone(String telefone);
    // Add other Aluno-specific query methods if needed
} 