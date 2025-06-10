package com.projeto.unify.dtos;

import com.projeto.unify.models.Solicitacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SolicitacaoDTO(
        @NotNull
        Solicitacao.TipoSolicitacao tipo,

        @NotBlank
        @Size(max = 1000)
        String mensagem
) {} 