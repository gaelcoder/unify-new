package com.projeto.unify.models;
import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private int creditos;

    @Column(nullable = false)
    private int cargaHoraria;

    @Column(nullable = true)
    private int creditosNecessarios;

    @ManyToMany
    @JoinTable(
            name = "materia_graduacao",
            joinColumns = @JoinColumn(name = "materia_id"),
            inverseJoinColumns = @JoinColumn(name = "graduacao_id")
    )
    private Set<Graduacao> graduacoes = new HashSet<>();

    @OneToMany(mappedBy = "materia")
    private List<Turma> turmas = new ArrayList<>();

    public Materia(String titulo, int creditos, int cargaHoraria) {
        this.titulo = titulo;
        this.creditos = creditos;
        this.cargaHoraria = cargaHoraria;
        this.creditosNecessarios = 0;
    }
}
