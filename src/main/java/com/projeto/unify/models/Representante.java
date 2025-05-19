package com.projeto.unify.models;

import com.projeto.unify.models.base.Pessoa;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "representantes")
public class Representante extends Pessoa {

    @Column(nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private String cargo;

    @OneToOne(mappedBy = "representante")
    private Universidade universidade;

    public Representante(String cpf, LocalDate dataNascimento, String nome, String sobrenome, String email, String telefone, String cargo) {
        super(cpf, dataNascimento, nome, sobrenome);
        this.cargo = cargo;
        this.email = email;
        this.telefone = telefone;
    }



}
