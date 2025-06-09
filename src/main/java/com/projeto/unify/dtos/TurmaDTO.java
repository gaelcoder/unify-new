package com.projeto.unify.dtos;

import com.projeto.unify.models.Aluno;
import lombok.Data;

import java.util.List;

@Data
public class TurmaDTO {
    private Long id;
    private String turno;
    private String campus;
    private Integer limiteAlunos;
    private MateriaDTO materia;
    private ProfessorDTO professor;
    private List<AlunoDTO> alunos;
} 