package com.projeto.unify.repositories;

import com.projeto.unify.models.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByTelefone(String telefone);
    Optional<Professor> findByCpf(String cpf);
    Optional<Professor> findByEmail(String email);
    Optional<Professor> findByTelefone(String telefone);
    // Add other Professor-specific query methods if needed
} 