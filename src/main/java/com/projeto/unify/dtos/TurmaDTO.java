package com.projeto.unify.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class TurmaDTO {

    private Long id; // Para respostas e atualizações

    @NotNull(message = "O ID da matéria é obrigatório.")
    private Long materiaId;
    private String nomeMateria; // Adicionado para exibição

    // O ID do professor é opcional ao criar a turma, pode ser atribuído depois.
    private Long professorId;
    private String nomeProfessor; // Adicionado para exibição

    @NotNull(message = "O ID da graduação é obrigatório.")
    private Long graduacaoId;
    private String nomeGraduacao; // Adicionado para exibição
    private String codigoGraduacao; // Adicionado para exibição

    @NotBlank(message = "O turno é obrigatório.")
    @Pattern(regexp = "^(MANHA|TARDE|NOITE)$", message = "Turno deve ser MANHA, TARDE ou NOITE")
    private String turno;

    @NotNull(message = "O limite de alunos é obrigatório.")
    @Min(value = 1, message = "O limite de alunos deve ser de pelo menos 1.")
    private Integer limiteAlunos;

    private Integer vagasDisponiveis; // Adicionado para exibição
    private Integer numeroAlunosMatriculados; // Adicionado para exibição

    // Não incluir List<Aluno> ou List<Avaliacao> aqui, gerenciados separadamente.
    // universidadeId será inferida pelo funcionário logado no serviço.
} 