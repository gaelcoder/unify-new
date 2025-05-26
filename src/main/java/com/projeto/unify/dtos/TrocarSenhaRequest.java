package com.projeto.unify.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrocarSenhaRequest {
    private Long id;
    private String novaSenha;
}