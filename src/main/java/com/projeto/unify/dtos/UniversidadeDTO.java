package com.projeto.unify.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class UniversidadeDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "CNPJ é obrigatório")
    private String cnpj;

    @NotNull(message = "Data de fundação é obrigatória")
    private LocalDate fundacao;

    @NotBlank(message = "Sigla é obrigatória")
    private String sigla;

    private List<String> campus;
}

