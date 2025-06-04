package com.projeto.unify.dtos;

import com.projeto.unify.models.Universidade;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UniversidadeCampusDTO {
    private Long id;
    private String nome;
    private List<String> campus;

    public UniversidadeCampusDTO(Universidade universidade) {
        this.id = universidade.getId();
        this.nome = universidade.getNome();
        // Ensure campus list is initialized and copied to avoid issues with lazy loading or nulls
        this.campus = universidade.getCampus() != null ? new ArrayList<>(universidade.getCampus()) : new ArrayList<>();
    }
} 