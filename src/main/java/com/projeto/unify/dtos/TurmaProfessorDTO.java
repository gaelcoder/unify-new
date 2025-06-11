package com.projeto.unify.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurmaProfessorDTO {
    private Long id;
    private String nomeMateria;
    private String turno;
    private String diaSemana;
    private String campus;
    private int numeroDeAlunos;
} 