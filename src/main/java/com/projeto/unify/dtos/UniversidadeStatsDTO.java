package com.projeto.unify.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UniversidadeStatsDTO {
    private String universidadeNome;
    private Long universidadeId;
    private int campusCount;
    private long funcionariosCount; // Includes all types of funcionarios initially
    private long alunosCount;       // Placeholder, to be implemented
} 