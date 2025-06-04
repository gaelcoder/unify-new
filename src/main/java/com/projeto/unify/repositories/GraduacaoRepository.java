package com.projeto.unify.repositories;

import com.projeto.unify.models.Graduacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import com.projeto.unify.models.Universidade;

@Repository
public interface GraduacaoRepository extends JpaRepository<Graduacao, Long> {
    // You can add custom query methods here if needed in the future
    List<Graduacao> findByUniversidade(Universidade universidade);
    Optional<Graduacao> findByIdAndUniversidade(Long id, Universidade universidade);
}
