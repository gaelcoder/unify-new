package com.projeto.unify.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FuncionarioDTO {
    private String cpf;
    private LocalDate dataNascimento;
    private String nome;
    private String sobrenome;
    private String email;
    private String telefone;
    private String setor;
    private Double salario;
}
