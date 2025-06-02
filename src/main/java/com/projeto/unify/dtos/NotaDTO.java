package com.projeto.unify.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotaDTO {

    private Long id; // Used for updates, null for creation

    @NotNull(message = "O ID do aluno é obrigatório")
    private Long alunoId;

    @NotNull(message = "O ID da avaliação é obrigatório")
    private Long avaliacaoId;

    @NotNull(message = "O valor obtido não pode ser nulo")
    @PositiveOrZero(message = "A nota deve ser positiva ou zero") // Assuming grades are not negative
    private Double valorObtido;

    private String observacoes;
} 