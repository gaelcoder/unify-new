package com.projeto.unify.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "universidades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Universidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Column(name = "data_fundacao", nullable = false)
    private LocalDate fundacao;

    @Column(length = 10)
    private String sigla;

    @ElementCollection
    @CollectionTable(name = "campus_list", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "campus")
    private List<String> campus = new ArrayList<>();

    public Universidade(String nome, String cnpj, LocalDate fundacao, String sigla) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.fundacao = fundacao;
        this.sigla = sigla;
    }

}
