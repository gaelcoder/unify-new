package com.projeto.unify.models;

import com.projeto.unify.models.base.Pessoa;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name="alunos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aluno extends Pessoa {

    @OneToOne
    private Graduacao graduacao;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String matricula;

    @Column(nullable = false)
    private String curso;

    @Column(nullable = false)
    private float cr;

    @ManyToOne
    @JoinColumn(name = "turma_id")
    private Turma turma;

    public Aluno(String cpf, LocalDate dataNasc, String nome, String sobrenome, Graduacao graduacao, String email, String matricula, String curso, float cr) {
        super(cpf, dataNasc, nome, sobrenome);
        this.graduacao = graduacao;
        this.email = email;
        this.matricula = matricula;
        this.curso = curso;
        this.cr = cr;
    }

}
