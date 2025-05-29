package com.projeto.unify.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "perfis")
@Getter
@Setter
@NoArgsConstructor
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TipoPerfil nome;

    public enum TipoPerfil {
        ROLE_ADMIN_GERAL,             // Administrador geral do sistema
        ROLE_ADMIN_UNIVERSIDADE,      // Administrador de uma universidade específica
        ROLE_FUNCIONARIO,             // Funcionário genérico da universidade
        ROLE_FUNCIONARIO_RH,          // Funcionário de RH
        ROLE_PROFESSOR,               // Professor
        ROLE_ALUNO                    // Aluno
    }

    public Perfil(TipoPerfil nome) {
        this.nome = nome;
    }
}