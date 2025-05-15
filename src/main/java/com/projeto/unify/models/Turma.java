package com.projeto.unify.models;

import com.projeto.unify.models.base.Pessoa;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="turmas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Professor professor;

    @Column


}
