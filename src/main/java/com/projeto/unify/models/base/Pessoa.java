package com.projeto.unify.models.base;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
public abstract class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String sobrenome;

    // Construtores
    protected Pessoa() {}

    protected Pessoa(String cpf, LocalDate dataNascimento, String nome, String sobrenome) {
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.nome = nome;
        this.sobrenome = sobrenome;
    }

    // Método útil para obter nome completo
    public String getNomeCompleto() {
        return nome + " " + sobrenome;
    }
}