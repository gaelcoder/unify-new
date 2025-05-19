package com.projeto.unify.models;
import jakarta.persistence.*;
import lombok.*;

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
    private int creditos;

    @Column(nullable = false)
    private int cargaHoraria;

    @Column(nullable = true)
    private int creditosNecessarios;

    public Materia(String titulo, int creditos, int cargaHoraria) {
        this.titulo = titulo;
        this.creditos = creditos;
        this.cargaHoraria = cargaHoraria;
        this.creditosNecessarios = 0;
    }

}
