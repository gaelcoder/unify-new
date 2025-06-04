package com.projeto.unify.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GraduacaoDTO {

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    @NotNull(message = "Número de semestres é obrigatório")
    @Min(value = 1, message = "Número de semestres deve ser no mínimo 1")
    private Integer semestres;

    @NotBlank(message = "Código do curso é obrigatório")
    private String codigoCurso;

    private Long coordenadorDoCursoId;

    private List<String> campiDisponiveis;
}
