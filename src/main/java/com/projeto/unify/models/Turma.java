package com.projeto.unify.models;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;


@Entity
@Table(name="turmas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graduacao_id", nullable = true)
    @JsonBackReference("graduacao-turmas")
    private Graduacao graduacao;

    @ManyToOne
    @JsonBackReference("professor-turmas")
    private Professor professor;

    @ManyToOne
    @JoinColumn(name = "materia_id", nullable = false)
    @NotNull(message = "A matéria é obrigatória")
    @JsonBackReference("materia-turmas")
    private Materia materia;

    @Column(nullable = false)
    @NotBlank(message = "O turno é obrigatório")
    @Pattern(regexp = "^(MANHA|TARDE|NOITE)$", message = "Turno deve ser MANHA, TARDE ou NOITE")
    private String turno;

    @Column(nullable = false)
    @NotBlank(message = "O campus é obrigatório")
    private String campus;

    @Column(nullable = false)
    @Min(value = 1, message = "O limite de alunos deve ser pelo menos 1")
    private int limiteAlunos;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "turma_aluno",
        joinColumns = @JoinColumn(name = "turma_id"),
        inverseJoinColumns = @JoinColumn(name = "aluno_id")
    )
    private List<Aluno> alunos = new ArrayList<>();

    public Turma(Materia materia, String turno, int limiteAlunos) {
        this.materia = Objects.requireNonNull(materia, "Matéria não pode ser nula");
        this.turno = Objects.requireNonNull(turno, "Turno não pode ser nulo");
        if (limiteAlunos <= 0) {
            throw new IllegalArgumentException("Limite de alunos deve ser positivo");
        }
        this.limiteAlunos = limiteAlunos;
    }


    public boolean adicionarAluno(Aluno aluno) {
        if (alunos.size() < limiteAlunos) {
            alunos.add(aluno);
            return true;
        }
        return false;
    }

    public void removerAluno(Aluno aluno) {
        if (aluno == null) {
            throw new IllegalArgumentException("Aluno não pode ser nulo");
        }
        alunos.remove(aluno);
    }

    public int getVagasDisponiveis() {
        return limiteAlunos - alunos.size();
    }

    public boolean alunoJaMatriculado(Aluno aluno) {
        return alunos.contains(aluno);
    }








}
