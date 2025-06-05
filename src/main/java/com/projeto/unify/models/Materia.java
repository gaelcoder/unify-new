package com.projeto.unify.models;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="materias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private int creditos;

    @Column(nullable = false)
    private int cargaHoraria;

    @Column(nullable = false)
    private int creditosNecessarios;

    @ManyToOne(optional = false)
    @JoinColumn(name = "universidade_id", nullable = false)
    @JsonIgnoreProperties({"graduacoes", "alunos", "professores", "funcionarios", "materias", "campus", "sigla", "cnpj", "telefone", "email", "logoPath", "hibernateLazyInitializer"})
    private Universidade universidade;

    @ManyToMany
    @JoinTable(
            name = "materia_graduacao",
            joinColumns = @JoinColumn(name = "materia_id"),
            inverseJoinColumns = @JoinColumn(name = "graduacao_id")
    )
    @JsonIgnoreProperties({"materias", "alunos", "universidade", "coordenadorDoCurso", "campusDisponiveis", "hibernateLazyInitializer"})
    private Set<Graduacao> graduacoes = new HashSet<>();

    @OneToMany(mappedBy = "materia")
    @JsonManagedReference("materia-turmas")
    private List<Turma> turmas = new ArrayList<>();

    public Materia(String titulo, String codigo, int creditos, int cargaHoraria, Universidade universidade) {
        this.titulo = titulo;
        this.codigo = codigo;
        this.creditos = creditos;
        this.cargaHoraria = cargaHoraria;
        this.universidade = universidade;
        this.creditosNecessarios = 0;
    }

}