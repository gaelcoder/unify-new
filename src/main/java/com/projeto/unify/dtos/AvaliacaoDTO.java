package com.projeto.unify.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoDTO {

    private Long id; // Used for updates, null for creation

    @NotNull(message = "O ID da turma é obrigatório")
    private Long turmaId;

    @NotBlank(message = "O nome da avaliação não pode estar em branco")
    private String nome;

    private LocalDate dataPrevista;

    @PositiveOrZero(message = "O valor máximo possível deve ser positivo ou zero")
    private Double valorMaximoPossivel;
} 