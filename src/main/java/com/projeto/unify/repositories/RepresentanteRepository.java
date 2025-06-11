package com.projeto.unify.repositories;

import com.projeto.unify.models.Representante;
import com.projeto.unify.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RepresentanteRepository extends JpaRepository<Representante, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByTelefone(String telefone);
    Optional<Representante> findByEmail(String email);
    Optional<Representante> findByCpf(String cpf);
    Optional<Representante> findByTelefone(String telefone);
    Optional<Representante> findByUsuario(Usuario usuario);
    Optional<Representante> findByUsuarioId(Long usuarioId);

    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByCpfAndIdNot(String cpf, Long id);
}
