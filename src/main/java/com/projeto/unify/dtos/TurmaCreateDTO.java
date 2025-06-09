package com.projeto.unify.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class TurmaCreateDTO {

    @NotNull(message = "ID da Matéria é obrigatório")
    private Long materiaId;

    @NotNull(message = "ID do Professor é obrigatório")
    private Long professorId;

    @NotBlank(message = "Turno é obrigatório")
    @Pattern(regexp = "^(MANHA|TARDE|NOITE)$", message = "Turno deve ser MANHA, TARDE ou NOITE")
    private String turno;

    @NotBlank(message = "Campus é obrigatório")
    private String campus;

    @NotNull(message = "Limite de alunos é obrigatório")
    @Min(value = 1, message = "Limite de alunos deve ser no mínimo 1")
    private Integer limiteAlunos;

    private List<Long> alunoIds;
} 