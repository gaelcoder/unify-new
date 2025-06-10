package com.projeto.unify.dtos;

import com.projeto.unify.models.Aluno;
import com.projeto.unify.models.Materia;
import com.projeto.unify.models.Professor;
import com.projeto.unify.models.Turma;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TurmaDTO {
    private Long id;
    private Materia materia;
    private Professor professor;
    private String turno;
    private String diaSemana;
    private String campus;
    private int limiteAlunos;
    private List<Aluno> alunos;

    public TurmaDTO(Turma turma) {
        this.id = turma.getId();
        this.materia = turma.getMateria();
        this.professor = turma.getProfessor();
        this.turno = turma.getTurno();
        this.diaSemana = turma.getDiaSemana();
        this.campus = turma.getCampus();
        this.limiteAlunos = turma.getLimiteAlunos();
        this.alunos = turma.getAlunos();
    }
} 