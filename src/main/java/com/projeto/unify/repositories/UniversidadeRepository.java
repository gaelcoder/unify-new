package com.projeto.unify.repositories;

import com.projeto.unify.models.Universidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;



@Repository
public interface UniversidadeRepository extends JpaRepository<Universidade, Long> {
    boolean existsByCnpj(String cnpj);
    Optional<Universidade> findByCnpj(String cnpj);
}
