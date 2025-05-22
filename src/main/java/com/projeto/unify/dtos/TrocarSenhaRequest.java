package com.projeto.unify.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrocarSenhaRequest {
    private String senhaAtual;
    private String novaSenha;
}