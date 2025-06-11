package com.projeto.unify.repositories;

import com.projeto.unify.models.Universidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;



@Repository
public interface UniversidadeRepository extends JpaRepository<Universidade, Long> {

    @Query("SELECT u FROM Universidade u LEFT JOIN FETCH u.representante")
    List<Universidade> findAllWithRepresentante();

    boolean existsByCnpj(String cnpj);
    Optional<Universidade> findByCnpj(String cnpj);
}
