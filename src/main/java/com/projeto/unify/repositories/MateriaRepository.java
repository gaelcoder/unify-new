package com.projeto.unify.repositories;

import com.projeto.unify.models.Materia;
import com.projeto.unify.models.Universidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {

    // Finds all Materias that are associated with at least one Graduacao
    // belonging to the specified Universidade. The DISTINCT keyword is important
    // to avoid duplicates if a Materia is linked to multiple Graduacoes of the same Universidade.
    List<Materia> findDistinctByGraduacoes_Universidade(Universidade universidade);

    // Optional: A method to directly check if a materia is linked to a specific university.
    // boolean existsByIdAndGraduacoes_Universidade(Long id, Universidade universidade);
}
