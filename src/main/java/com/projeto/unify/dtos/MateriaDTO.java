package com.projeto.unify.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class MateriaDTO {

    private Long id; // Optional: Used for response, not typically for create/update request directly unless specified

    @NotBlank(message = "Título da matéria é obrigatório.")
    private String titulo;

    @NotBlank(message = "Código da matéria é obrigatório.")
    private String codigo;

    @NotNull(message = "Créditos são obrigatórios.")
    @Positive(message = "Créditos devem ser um número positivo.")
    private Integer creditos;

    @NotNull(message = "Carga horária é obrigatória.")
    @Positive(message = "Carga horária deve ser um número positivo.")
    private Integer cargaHoraria;

    private String ementa; // Syllabus/Description - optional

    private Integer creditosNecessarios; // Prerequisite credits - optional, defaults to 0 if not provided

    // Assuming universidadeId will be derived from the logged-in Funcionario (Secretary)
    // If it needs to be specified in the DTO (e.g., for a super admin), uncomment below
    // private Long universidadeId;

    @NotEmpty(message = "A matéria deve ser associada a pelo menos uma graduação.")
    private Set<Long> graduacaoIds;
} 