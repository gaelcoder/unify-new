package com.projeto.unify.repositories;

import com.projeto.unify.models.TransferenciaGraduacaoSolicitacao;
import com.projeto.unify.models.enums.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferenciaGraduacaoSolicitacaoRepository extends JpaRepository<TransferenciaGraduacaoSolicitacao, Long> {

    List<TransferenciaGraduacaoSolicitacao> findByUniversidadeIdAndStatusSolicitacao(Long universidadeId, StatusSolicitacao status);

    Optional<TransferenciaGraduacaoSolicitacao> findByIdAndUniversidadeId(Long id, Long universidadeId);

    @Query("SELECT tgs FROM TransferenciaGraduacaoSolicitacao tgs WHERE tgs.aluno.id = :alunoId AND tgs.graduacaoDesejada.id = :graduacaoDesejadaId AND tgs.statusSolicitacao = 'PENDENTE'")
    Optional<TransferenciaGraduacaoSolicitacao> findByAlunoIdAndGraduacaoDesejadaIdAndStatusPendente(@Param("alunoId") Long alunoId, @Param("graduacaoDesejadaId") Long graduacaoDesejadaId);
    
    List<TransferenciaGraduacaoSolicitacao> findByAlunoIdAndStatusSolicitacao(Long alunoId, StatusSolicitacao status);

} 