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
    private String email; // Assuming email is needed for creation
    private String setor;
    private Double salario;
    // universidadeId will not be in the DTO for creation via ADMIN_UNIVERSIDADE,
    // as it will be automatically assigned.
}
