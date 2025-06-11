package com.projeto.unify.dtos;

import com.projeto.unify.models.Solicitacao;

public record SolicitacaoSecretariaDTO(
    Long id,
    String protocolo,
    String matriculaAluno,
    String nomeAluno,
    Solicitacao.TipoSolicitacao tipo,
    String campus
) {
} 