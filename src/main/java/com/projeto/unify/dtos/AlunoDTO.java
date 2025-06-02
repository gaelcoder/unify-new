package com.projeto.unify.dtos;

// import com.projeto.unify.models.Graduacao; // Não é mais necessário se curso for removido e graduacaoId for usado
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.DecimalMin; // CR removed
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AlunoDTO {

    @NotBlank(message = "Nome não pode estar em branco")
    private String nome;

    @NotBlank(message = "Sobrenome não pode estar em branco")
    private String sobrenome;

    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email não pode estar em branco")
    private String email;

    @NotBlank(message = "CPF não pode estar em branco")
    private String cpf;

    @NotNull(message = "Data de nascimento não pode ser nula")
    private LocalDate dataNascimento;

    @NotBlank(message = "Telefone não pode estar em branco")
    private String telefone;

    @NotNull(message = "ID da graduação não pode ser nulo")
    private Long graduacaoId;
    
    private Long turmaId;

}
