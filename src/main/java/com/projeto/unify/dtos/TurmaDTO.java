package com.projeto.unify.dtos;

import com.projeto.unify.models.Turma;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurmaDTO {
    private Long id;
    private String nomeMateria;
    private String nomeProfessor;
    private String turno;
    private String campus;
    private int limiteAlunos;
    private int vagasDisponiveis;

    public TurmaDTO(Turma turma) {
        this.id = turma.getId();
        this.nomeMateria = turma.getMateria() != null ? turma.getMateria().getTitulo() : "N/A";
        this.nomeProfessor = turma.getProfessor() != null ? turma.getProfessor().getNome() : "N/A";
        this.turno = turma.getTurno();
        this.campus = turma.getCampus();
        this.limiteAlunos = turma.getLimiteAlunos();
        this.vagasDisponiveis = turma.getVagasDisponiveis();
    }
} 