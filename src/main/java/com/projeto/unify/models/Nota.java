package com.projeto.unify.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    @NotNull(message = "O aluno é obrigatório para a nota")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avaliacao_id", nullable = false)
    @NotNull(message = "A avaliação é obrigatória para a nota")
    private Avaliacao avaliacao;

    @NotNull(message = "O valor obtido não pode ser nulo")
    @Column(nullable = false)
    private Double valorObtido;

    @Column(nullable = false)
    private LocalDateTime dataLancamento;

    @Column(length = 500) // Definindo um tamanho para observações
    private String observacoes;

    @PrePersist
    protected void onCreate() {
        this.dataLancamento = LocalDateTime.now();
    }

    // Construtores, getters, setters são gerenciados pelo Lombok
} 