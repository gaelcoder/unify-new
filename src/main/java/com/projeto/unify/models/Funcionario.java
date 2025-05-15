package com.projeto.unify.models;

import com.projeto.unify.models.base.Pessoa;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Funcionario extends Pessoa{

    private String departamento;

    @ManyToOne
    @JoinColumn(name = "universidade_id", nullable = false)
    private Universidade universidade;

    private double salario;

    public Funcionario(){
        super();
    }

    public Funcionario(String cpf, LocalDate dataNasc, String nome, String sobrenome, String departamento, double salario){
        super(cpf, dataNasc, nome, sobrenome);
        this.departamento = departamento;
        this.salario
    }

}