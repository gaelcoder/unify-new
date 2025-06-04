package com.projeto.unify.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.DecimalMin; // Assuming this was intentionally commented
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AlunoDTO {

    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @NotNull(message = "Data de nascimento é obrigatória")
    private LocalDate dataNascimento;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Sobrenome é obrigatório")
    private String sobrenome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    // O campo 'endereco' foi removido pois não existe na entidade Pessoa/Aluno.

    @NotNull(message = "ID da Graduação é obrigatório") // Graduacao é obrigatória
    private Long graduacaoId;

    private Long universidadeId; // Opcional: usado se o criador não for funcionário com universidade associada

    // Campos como 'turmaId', 'curso', 'cr' foram intencionalmente omitidos:
    // - 'turmaId': Associação com Turma será posterior.
    // - 'curso': Coberto pela Graduacao.
    // - 'cr': Será inicializado como 0 e calculado posteriormente.

}
