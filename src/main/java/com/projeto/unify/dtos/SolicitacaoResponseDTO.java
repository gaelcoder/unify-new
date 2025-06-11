package com.projeto.unify.dtos;

import com.projeto.unify.models.Solicitacao.StatusSolicitacao;
import com.projeto.unify.models.Solicitacao.TipoSolicitacao;

public record SolicitacaoResponseDTO(
    Long id,
    TipoSolicitacao tipo,
    String mensagem,
    StatusSolicitacao status,
    String dataSolicitacao
) {} 