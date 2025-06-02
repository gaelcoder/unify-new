package com.projeto.unify.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraduacaoDTO {

    private Long id; // Para respostas e atualizações

    @NotBlank(message = "O título da graduação é obrigatório.")
    @Size(min = 3, max = 255, message = "O título deve ter entre 3 e 255 caracteres.")
    private String titulo;

    @NotNull(message = "O número de semestres é obrigatório.")
    @Min(value = 1, message = "A graduação deve ter pelo menos 1 semestre.")
    private Integer semestres;

    @NotBlank(message = "O código do curso é obrigatório.")
    @Size(min = 1, max = 50, message = "O código do curso deve ter entre 1 e 50 caracteres.")
    private String codigoCurso; // Ex: ADS, SI, ENGCOMP - deve ser único por universidade

    private Long coordenadorDoCursoId; // ID do Professor que será o coordenador

    @NotBlank(message = "A informação sobre o campus é obrigatória (ex: Principal, EAD, Nome do Campus).")
    private String campusDisponivel;

    // Não incluímos universidadeId aqui, pois será inferido pelo usuário logado (secretaria).
    // Não incluímos listas de matérias ou alunos, pois são gerenciadas separadamente.
} 