package com.projeto.unify.models;

import jakarta.persistence.*;
import lombok.*;


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

    private String campusDisponivel;

    public Graduacao(String titulo, int semestres, String codigoCurso) {
        this.titulo = titulo;
        this.semestres = semestres;
        this.codigoCurso = codigoCurso;
    }

}
