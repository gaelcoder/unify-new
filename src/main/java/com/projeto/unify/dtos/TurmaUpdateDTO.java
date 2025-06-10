package com.projeto.unify.dtos;

import lombok.Data;

import java.util.List;

@Data
public class TurmaUpdateDTO {
    private Long professorId;
    private List<Long> alunoIds;
} 