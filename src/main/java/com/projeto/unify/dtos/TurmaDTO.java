package com.projeto.unify.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurmaDTO {

    @NotNull(message = "ID da Matéria é obrigatório")
    private Long materiaId;

    private Long professorId; // Opcional, pode ser atribuído depois

    @NotBlank(message = "Turno é obrigatório")
    @Pattern(regexp = "^(MANHA|TARDE|NOITE)$", message = "Turno deve ser MANHA, TARDE ou NOITE")
    private String turno;

    @NotNull(message = "Limite de alunos é obrigatório")
    @Min(value = 1, message = "Limite de alunos deve ser no mínimo 1")
    private Integer limiteAlunos;

    @NotNull(message = "ID da Graduação é obrigatório")
    private Long graduacaoId; // Turma está diretamente ligada a uma Graduação

    // Outros campos como ano, semestre, codigoDaTurma podem ser adicionados se necessário.
    // Por enquanto, baseando-se no modelo Turma.java existente.
} 