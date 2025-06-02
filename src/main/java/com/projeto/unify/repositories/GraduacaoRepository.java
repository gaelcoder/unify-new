package com.projeto.unify.repositories;

import com.projeto.unify.models.Graduacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GraduacaoRepository extends JpaRepository<Graduacao, Long> {

    List<Graduacao> findByUniversidadeId(Long universidadeId);

    boolean existsByCodigoCursoAndUniversidadeId(String codigoCurso, Long universidadeId);

    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN TRUE ELSE FALSE END FROM Graduacao g WHERE g.codigoCurso = :codigoCurso AND g.universidade.id = :universidadeId AND g.id <> :graduacaoId")
    boolean existsByCodigoCursoAndUniversidadeIdAndIdNot(@Param("codigoCurso") String codigoCurso, @Param("universidadeId") Long universidadeId, @Param("graduacaoId") Long graduacaoId);

    Optional<Graduacao> findByCodigoCursoAndUniversidadeId(String codigoCurso, Long universidadeId);

}
