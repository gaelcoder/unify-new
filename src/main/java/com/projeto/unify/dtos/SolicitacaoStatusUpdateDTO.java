package com.projeto.unify.dtos;

import com.projeto.unify.models.Solicitacao;
import jakarta.validation.constraints.NotNull;

public record SolicitacaoStatusUpdateDTO(
    @NotNull
    Solicitacao.StatusSolicitacao status
) {
} 