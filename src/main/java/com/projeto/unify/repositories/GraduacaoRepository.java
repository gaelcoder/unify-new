package com.projeto.unify.repositories;

import com.projeto.unify.models.Graduacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import com.projeto.unify.models.Universidade;

@Repository
public interface GraduacaoRepository extends JpaRepository<Graduacao, Long> {
    // You can add custom query methods here if needed in the future
    List<Graduacao> findByUniversidade(Universidade universidade);
    Optional<Graduacao> findByIdAndUniversidade(Long id, Universidade universidade);

    @Query("SELECT DISTINCT g.coordenadorDoCurso.id FROM Graduacao g WHERE g.universidade.id = :universidadeId AND g.coordenadorDoCurso IS NOT NULL")
    List<Long> findDistinctCoordenadorDoCursoIdByUniversidadeId(@Param("universidadeId") Long universidadeId);

    Optional<Graduacao> findByTitulo(String titulo);
    List<Graduacao> findByUniversidadeId(Long universidadeId);
    Optional<Graduacao> findByIdAndUniversidadeId(Long id, Long universidadeId);

    @Query("SELECT DISTINCT g.campusDisponiveis FROM Graduacao g JOIN g.materias m WHERE m.id = :materiaId")
    List<String> findCampusesByMateriaId(@Param("materiaId") Long materiaId);
}
