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
        ROLE_ADMIN_GERAL,
        ROLE_ADMIN_UNIVERSIDADE,
        ROLE_PROFESSOR,
        ROLE_ALUNO,
        ROLE_FUNCIONARIO;
    }
}