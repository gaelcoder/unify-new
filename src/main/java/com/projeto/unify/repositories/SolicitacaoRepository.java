package com.projeto.unify.repositories;

import com.projeto.unify.models.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
    List<Solicitacao> findByAlunoId(Long alunoId);

    List<Solicitacao> findByUniversidadeIdAndStatus(Long universidadeId, Solicitacao.StatusSolicitacao status);

    List<Solicitacao> findByUniversidadeIdAndAluno_CampusAndStatus(Long universidadeId, String campus, Solicitacao.StatusSolicitacao status);
} 