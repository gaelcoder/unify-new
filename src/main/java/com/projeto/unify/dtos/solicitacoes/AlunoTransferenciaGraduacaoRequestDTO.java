package com.projeto.unify.dtos.solicitacoes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlunoTransferenciaGraduacaoRequestDTO {

    // alunoId will be taken from the authenticated user (student)

    @NotNull(message = "O ID da graduação atual é obrigatório.")
    private Long graduacaoAtualId;

    @NotNull(message = "O ID da graduação desejada é obrigatório.")
    private Long graduacaoDesejadaId;

    @NotBlank(message = "A justificativa é obrigatória.")
    private String justificativaAluno;
} 