package com.projeto.unify.models;

import com.projeto.unify.models.base.Pessoa;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "aluno")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aluno extends Pessoa {

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String telefone;

    @Column(nullable = false)
    private String matricula;

    @Column
    private float cr;

    @Column
    private String campus;

    @ManyToMany(mappedBy = "alunos")
    @JsonBackReference("turma-alunos")
    private List<Turma> turmas = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "universidade_id", nullable = false)
    @JsonBackReference("universidade-alunos")
    private Universidade universidade;

    @ManyToOne
    @JoinColumn(name = "graduacao_id")
    @JsonBackReference("graduacao-alunos")
    private Graduacao graduacao;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


    public Aluno(String cpf, LocalDate dataNasc, String nome, String sobrenome, Graduacao graduacao, String email, String telefone, String matricula, float cr, String campus) {
        super(cpf, dataNasc, nome, sobrenome);
        this.graduacao = graduacao;
        this.email = email;
        this.telefone = telefone;
        this.matricula = matricula;
        this.cr = 0;
        this.campus = campus;
    }

}
