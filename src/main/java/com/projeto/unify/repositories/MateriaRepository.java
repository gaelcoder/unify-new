package com.projeto.unify.repositories;

import com.projeto.unify.models.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {

    Optional<Materia> findByTitulo(String titulo);

    // Verifica se existe uma matéria com o mesmo título associada a qualquer graduação de uma universidade específica
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END FROM Materia m JOIN m.graduacoes g WHERE m.titulo = :titulo AND g.universidade.id = :universidadeId")
    boolean existsByTituloAndUniversidade(@Param("titulo") String titulo, @Param("universidadeId") Long universidadeId);

    // Lista matérias associadas a qualquer graduação de uma universidade específica
    @Query("SELECT DISTINCT m FROM Materia m JOIN m.graduacoes g WHERE g.universidade.id = :universidadeId")
    List<Materia> findByUniversidadeId(@Param("universidadeId") Long universidadeId);

    // Verifica se existe OUTRA matéria com o mesmo título na mesma universidade
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END FROM Materia m JOIN m.graduacoes g WHERE m.titulo = :titulo AND g.universidade.id = :universidadeId AND m.id <> :materiaId")
    boolean existsByTituloAndUniversidadeAndIdNot(@Param("titulo") String titulo, @Param("universidadeId") Long universidadeId, @Param("materiaId") Long materiaId);

    // Você pode adicionar outras consultas específicas aqui se necessário
}
