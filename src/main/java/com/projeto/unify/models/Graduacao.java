package com.projeto.unify.models;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;


@Entity
@Table(name = "graduacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Graduacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String titulo;

    @Column
    private int semestres;

    @Column
    private String codigoCurso;

    @OneToOne
    private Professor coordenadorDoCurso;

    @ManyToOne
    @JoinColumn(name = "universidade_id", nullable = false)
    @JsonBackReference("universidade-graduacoes")
    private Universidade universidade;

    @ManyToMany(mappedBy = "graduacoes")
    @JsonIgnoreProperties("graduacoes")
    private Set<Materia> materias = new HashSet<>();

    @OneToMany(mappedBy = "graduacao")
    @JsonManagedReference("graduacao-alunos")
    private List<Aluno> alunos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "graduacao_campus_disponiveis", joinColumns = @JoinColumn(name = "graduacao_id"))
    @Column(name = "campus")
    private List<String> campusDisponiveis = new ArrayList<>();

    public Graduacao(String titulo, int semestres, String codigoCurso, Universidade universidade) {
        this.titulo = titulo;
        this.semestres = semestres;
        this.codigoCurso = codigoCurso;
        this.universidade = universidade;
    }

}
