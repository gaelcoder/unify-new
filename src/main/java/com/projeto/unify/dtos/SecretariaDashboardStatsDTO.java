package com.projeto.unify.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecretariaDashboardStatsDTO {
    private long pendingTrocaTurmaSolicitacoes;
    private long pendingTransferenciaGraduacaoSolicitacoes;
    // Add more stats here as needed, e.g., recent activities
} 