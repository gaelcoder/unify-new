package com.projeto.unify.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column(name = "logo_path")
    private String logoPath;

    @ElementCollection
    @CollectionTable(name = "universidade_campus",
                     joinColumns = @JoinColumn(name = "universidade_id"))
    @Column(name = "campus_nome")
    private List<String> campus = new ArrayList<>();

    @OneToMany(mappedBy = "universidade", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Aluno> alunos = new ArrayList<>();

    @OneToMany(mappedBy = "universidade", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Funcionario> funcionarios = new ArrayList<>();

    @OneToMany(mappedBy = "universidade", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Professor> professores = new ArrayList<>();

    @OneToMany(mappedBy = "universidade", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Graduacao> graduacoes = new ArrayList<>();

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "representante_id")
    @JsonManagedReference("universidade-representante")
    private Representante representante;

    public Universidade(String nome, String cnpj, LocalDate fundacao, String sigla, String logoPath, Representante representante, List<String> campus
    ) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.fundacao = fundacao;
        this.sigla = sigla;
        this.logoPath = logoPath;
        this.representante = representante;
        this.campus = campus;
        this.graduacoes = new ArrayList<>();
    }
}