package com.projeto.unify.dtos.solicitacoes;

import com.projeto.unify.models.enums.StatusSolicitacao;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProcessarTransferenciaGraduacaoDTO {
    @NotNull(message = "A decisão (aprovar/rejeitar) é obrigatória.")
    private StatusSolicitacao novaStatus;

    private String observacaoSecretaria;
} 