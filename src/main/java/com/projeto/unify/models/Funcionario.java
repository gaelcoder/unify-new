package com.projeto.unify.models;

import com.projeto.unify.models.base.Pessoa;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "funcionarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario extends Pessoa {

    @Column(nullable = false)
    private String setor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidade_id")
    @JsonBackReference("universidade-funcionarios")
    private Universidade universidade;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String telefone;

    @Column(nullable = false)
    private double salario;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Funcionario(String cpf, LocalDate dataNasc, String nome, String sobrenome, String setor, double salario, String email, String telefone, Universidade universidade) {
        super(cpf, dataNasc, nome, sobrenome);
        this.setor = setor;
        this.salario = salario;
        this.email = email;
        this.telefone = telefone;
        this.universidade = universidade;
    }

    @Override
    public String toString() {
        return "Funcionario{" +
                "id=" + getId() +
                ", nome='" + getNome() + '\'' +
                ", sobrenome='" + getSobrenome() + '\'' +
                ", cpf='" + getCpf() + '\'' +
                ", dataNasc=" + getDataNascimento() +
                ", setor='" + setor + '\'' +
                ", salario=" + salario +
                ", universidade=" + (universidade != null ? universidade.getNome() : "N/A") +
                '}';
    }
}