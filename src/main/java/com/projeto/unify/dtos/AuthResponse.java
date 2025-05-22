package com.projeto.unify.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String tipo;  // Tipo de usuário (ADMIN_GERAL, ADMIN_UNIVERSIDADE, etc)
    private boolean primeiroAcesso;
    private String mensagem;
}