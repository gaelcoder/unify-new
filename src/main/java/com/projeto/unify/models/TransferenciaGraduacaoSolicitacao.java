package com.projeto.unify.models;

import com.projeto.unify.models.enums.StatusSolicitacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "transferencia_graduacao_solicitacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaGraduacaoSolicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graduacao_atual_id", nullable = false)
    private Graduacao graduacaoAtual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graduacao_desejada_id", nullable = false)
    private Graduacao graduacaoDesejada;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String justificativaAluno;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSolicitacao statusSolicitacao;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataSolicitacao;

    @Column
    private LocalDateTime dataProcessamento;

    @Column(columnDefinition = "TEXT")
    private String observacaoSecretaria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidade_id", nullable = false)
    private Universidade universidade;

    @PrePersist
    protected void onCreate() {
        if (this.aluno != null) {
            this.universidade = this.aluno.getUniversidade();
        }
        this.statusSolicitacao = StatusSolicitacao.PENDENTE;
    }
} 