package com.projeto.unify.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao")
@Getter
@Setter
public class Solicitacao {

    public enum TipoSolicitacao {
        TRC("TRC - Trancamento de Matrícula"),
        TFG("TFG - Transferência de Graduação"),
        TRM("TRM - Troca de Turma"),
        HIST("EHE - Emissão de Histórico Escolar"),
        MAT("EMT - Emissão de Declaração de Matrícula");

        private final String descricao;

        TipoSolicitacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum StatusSolicitacao {
        ABERTA,
        CONCLUIDA,
        REJEITADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSolicitacao tipo;

    @Lob
    @Column(nullable = false, length = 1000)
    private String mensagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSolicitacao status;

    @Column(nullable = false)
    private LocalDateTime dataSolicitacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidade_id", nullable = false)
    private Universidade universidade;

} 