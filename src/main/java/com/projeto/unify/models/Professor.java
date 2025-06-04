package com.projeto.unify.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.projeto.unify.models.base.Pessoa;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;



@Entity
@Table(name = "professores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Professor extends Pessoa {

    @Column(nullable = false)
    private double salario;

    @Column(nullable = false)
    private String titulacao;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String telefone;

    @OneToMany(mappedBy = "professor", fetch = FetchType.LAZY)
    @JsonManagedReference("professor-turmas")
    private Set<Turma> turmas = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidade_id")
    @JsonBackReference("universidade-professores")
    private Universidade universidade;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Professor(String cpf, LocalDate dataNasc, String nome, String sobrenome, String setor, double salario, Universidade universidade, String titulacao, String email, String telefone) {
        super(cpf, dataNasc, nome, sobrenome);
        this.salario = salario;
        this.titulacao = titulacao;
        this.email = email;
        this.telefone = telefone;
        this.universidade = universidade;
    }

}
