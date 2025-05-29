package com.projeto.unify.repositories;

import com.projeto.unify.models.Funcionario;
import com.projeto.unify.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    Optional<Funcionario> findByEmail(String email);
    Optional<Funcionario> findByCpf(String cpf);
    Optional<Funcionario> findByUsuario(Usuario usuario);
    Optional<Funcionario> findByUsuarioId(Long usuarioId);
}
