package com.projeto.unify.models;

import com.projeto.unify.models.base.Pessoa;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "funcionarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario extends Pessoa{

    @Column(nullable = false)
    private String setor;

    @ManyToOne
    @JoinColumn(name = "universidade_id", nullable = false)
    private Universidade universidade;

    @Column(nullable = false)
    private double salario;

    public Funcionario(String cpf, LocalDate dataNasc, String nome, String sobrenome, String setor, double salario, Universidade universidade){
        super(cpf, dataNasc, nome, sobrenome);
        this.setor = setor;
        this.salario = salario;
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
                ", universidade=" + universidade.getNome() +
                '}';
    }



}