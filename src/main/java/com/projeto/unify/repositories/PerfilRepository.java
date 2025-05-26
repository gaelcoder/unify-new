package com.projeto.unify.repositories;

import com.projeto.unify.models.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {
    Optional<Perfil> findByNome(Perfil.TipoPerfil nome);
}