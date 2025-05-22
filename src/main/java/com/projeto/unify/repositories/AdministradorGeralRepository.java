package com.projeto.unify.repositories;

import com.projeto.unify.models.AdministradorGeral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorGeralRepository extends JpaRepository<AdministradorGeral, Long> {
}