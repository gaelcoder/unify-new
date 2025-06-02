package com.projeto.unify.dtos.solicitacoes;

import com.projeto.unify.models.enums.StatusSolicitacao;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TrocaTurmaSolicitacaoResponseDTO {
    private Long id;
    private Long alunoId;
    private String alunoNome;
    private String alunoMatricula;
    private Long turmaAtualId;
    private String turmaAtualDescricao; // e.g., "Materia X - Graduacao Y - Turno Z"
    private Long turmaDesejadaId;
    private String turmaDesejadaDescricao;
    private String justificativaAluno;
    private StatusSolicitacao statusSolicitacao;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataProcessamento;
    private String observacaoSecretaria;
    private Long universidadeId;
    private String universidadeNome;
} 