package com.projeto.unify.models;

import java.util.List;
import java.util.ArrayList;
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

    @OneToOne
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @Column(nullable = false)
    private String turno;

    @Column(nullable = false)
    private int limiteAlunos;

    @OneToMany
    @JoinColumn(name = "turma_id")
    private List<Aluno> alunos = new ArrayList<>();

    public Turma(Materia materia, String turno, int limiteAlunos){
        this.materia = materia;
        this.turno = turno;
        this.limiteAlunos = limiteAlunos;
    }

    public boolean adicionarAluno(Aluno aluno) {
        if (alunos.size() < limiteAlunos) {
            alunos.add(aluno);
            return true;
        }
        return false;
    }

    public void removerAluno(Aluno aluno) {
        alunos.remove(aluno);
    }

    public int getVagasDisponiveis() {
        return limiteAlunos - alunos.size();
    }





}
