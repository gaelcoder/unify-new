package com.projeto.unify.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class MateriaDTO {

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    @NotNull(message = "Créditos são obrigatórios")
    @Min(value = 1, message = "Créditos devem ser no mínimo 1")
    private Integer creditos;

    @NotNull(message = "Carga horária é obrigatória")
    @Min(value = 1, message = "Carga horária deve ser no mínimo 1")
    private Integer cargaHoraria;

    @Min(value = 0, message = "Créditos necessários não podem ser negativos")
    private Integer creditosNecessarios; // Opcional, pode ser 0

    @NotNull(message = "IDs das graduações são obrigatórios")
    @Size(min = 1, message = "A matéria deve ser associada a pelo menos uma graduação")
    private Set<Long> graduacaoIds; // IDs das Graduacoes às quais esta matéria pertence

} 