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
    private int CargaHoraria;

    @Column(nullable = false)
    private String descricao;



}
