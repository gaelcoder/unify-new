package com.projeto.unify.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MateriaDTO {

    private Long id; // Para atualizações

    @NotBlank(message = "O título da matéria é obrigatório")
    private String titulo;

    @NotNull(message = "Os créditos da matéria são obrigatórios")
    @Min(value = 1, message = "A matéria deve ter pelo menos 1 crédito")
    private Integer creditos;

    @NotNull(message = "A carga horária da matéria é obrigatória")
    @Min(value = 1, message = "A carga horária deve ser de pelo menos 1 hora")
    private Integer cargaHoraria;

    private Integer creditosNecessarios; // Pode ser 0 ou nulo se não houver pré-requisito de créditos

    @NotNull(message = "A nota mínima para aprovação é obrigatória")
    private Double notaMinimaAprovacao;

    // IDs das graduações às quais esta matéria será associada.
    // Ao criar/atualizar uma matéria, o serviço validará se estas graduações
    // pertencem à universidade do funcionário logado.
    @NotEmpty(message = "A matéria deve ser associada a pelo menos uma graduação")
    private Set<Long> graduacaoIds;

} 