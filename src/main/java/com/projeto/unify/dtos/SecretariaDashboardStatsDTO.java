package com.projeto.unify.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecretariaDashboardStatsDTO {
    private long pendingTrocaTurmaSolicitacoes;
    private long pendingTransferenciaGraduacaoSolicitacoes;
    // Adicionar outros campos de estatísticas conforme necessário no futuro
} 