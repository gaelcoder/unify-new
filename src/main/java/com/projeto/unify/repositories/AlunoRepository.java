package com.projeto.unify.repositories;

import com.projeto.unify.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByTelefone(String telefone);
    long countByUniversidadeAndGraduacao(Universidade universidade, Graduacao graduacao);
    Optional<Aluno> findByCpf(String cpf);
    Optional<Aluno> findByEmail(String email);
    Optional<Aluno> findByTelefone(String telefone);
    List<Aluno> findByUniversidade(Universidade universidade);
    Optional<Aluno> findByUsuario(Usuario usuario);
    Optional<Aluno> findByUsuarioId(Long usuarioId);
} 