package com.projeto.unify.dtos.solicitacoes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlunoTrocaTurmaRequestDTO {

    // alunoId will be taken from the authenticated user (student)

    @NotNull(message = "O ID da turma atual é obrigatório.")
    private Long turmaAtualId;

    @NotNull(message = "O ID da turma desejada é obrigatório.")
    private Long turmaDesejadaId;

    @NotBlank(message = "A justificativa é obrigatória.")
    private String justificativaAluno;
} 