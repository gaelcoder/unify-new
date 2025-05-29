package com.projeto.unify.models;

import com.projeto.unify.models.base.Pessoa;
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
    private Universidade universidade;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private double salario;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Funcionario(String cpf, LocalDate dataNasc, String nome, String sobrenome, String setor, double salario, String email, Universidade universidade) {
        super(cpf, dataNasc, nome, sobrenome);
        this.setor = setor;
        this.salario = salario;
        this.email = email;
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