package com.projeto.unify.repositories;

import com.projeto.unify.models.TrocaTurmaSolicitacao;
import com.projeto.unify.models.enums.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrocaTurmaSolicitacaoRepository extends JpaRepository<TrocaTurmaSolicitacao, Long> {

    List<TrocaTurmaSolicitacao> findByUniversidadeIdAndStatusSolicitacao(Long universidadeId, StatusSolicitacao status);

    Optional<TrocaTurmaSolicitacao> findByIdAndUniversidadeId(Long id, Long universidadeId);

    // Query to find pending requests for a specific student to avoid duplicate requests for the same desired class
    @Query("SELECT tts FROM TrocaTurmaSolicitacao tts WHERE tts.aluno.id = :alunoId AND tts.turmaDesejada.id = :turmaDesejadaId AND tts.statusSolicitacao = 'PENDENTE'")
    Optional<TrocaTurmaSolicitacao> findByAlunoIdAndTurmaDesejadaIdAndStatusPendente(@Param("alunoId") Long alunoId, @Param("turmaDesejadaId") Long turmaDesejadaId);

    // Find pending requests for a student
    List<TrocaTurmaSolicitacao> findByAlunoIdAndStatusSolicitacao(Long alunoId, StatusSolicitacao status);
} 