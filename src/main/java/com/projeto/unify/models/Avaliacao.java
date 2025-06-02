package com.projeto.unify.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "avaliacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id", nullable = false)
    @NotNull(message = "A turma é obrigatória para a avaliação")
    private Turma turma;

    @NotBlank(message = "O nome da avaliação não pode estar em branco")
    @Column(nullable = false)
    private String nome; // ex: "Prova 1", "Trabalho Semestral"

    @Column(nullable = true)
    private LocalDate dataPrevista;

    @Column(nullable = true) // Pode ser nulo se a avaliação não tiver um teto específico (ex: participação)
    private Double valorMaximoPossivel; // ex: 10.0

    // Relacionamento com as notas lançadas para esta avaliação
    @OneToMany(mappedBy = "avaliacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Nota> notasLancadas = new ArrayList<>();

    // Construtor, getters, setters são gerenciados pelo Lombok
    // Adicionar métodos utilitários se necessário, por exemplo, para adicionar uma nota
    public void adicionarNota(Nota nota) {
        this.notasLancadas.add(nota);
        nota.setAvaliacao(this);
    }

    public void removerNota(Nota nota) {
        this.notasLancadas.remove(nota);
        nota.setAvaliacao(null);
    }
} 