package com.projeto.unify.dtos;

import com.projeto.unify.models.Solicitacao.TipoSolicitacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SolicitacaoCreateDTO(
    @NotNull(message = "O tipo de solicitação não pode ser nulo.")
    TipoSolicitacao tipo,
    
    @NotBlank(message = "A mensagem não pode estar em branco.")
    String mensagem,
    
    @NotNull(message = "O ID do aluno não pode ser nulo.")
    Long alunoId,
    
    @NotNull(message = "O ID da universidade não pode ser nulo.")
    Long universidadeId
) {} 