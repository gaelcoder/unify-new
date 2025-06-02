package com.projeto.unify.dtos.solicitacoes;

import com.projeto.unify.models.enums.StatusSolicitacao;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransferenciaGraduacaoSolicitacaoResponseDTO {
    private Long id;
    private Long alunoId;
    private String alunoNome;
    private String alunoMatricula;
    private Long graduacaoAtualId;
    private String graduacaoAtualNome;
    private String graduacaoAtualCodigo;
    private Long graduacaoDesejadaId;
    private String graduacaoDesejadaNome;
    private String graduacaoDesejadaCodigo;
    private String justificativaAluno;
    private StatusSolicitacao statusSolicitacao;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataProcessamento;
    private String observacaoSecretaria;
    private Long universidadeId;
    private String universidadeNome;
} 