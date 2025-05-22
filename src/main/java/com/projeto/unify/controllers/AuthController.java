package com.projeto.unify.controllers;

import com.projeto.unify.dtos.AuthResponse;
import com.projeto.unify.dtos.LoginRequest;
import com.projeto.unify.dtos.TrocarSenhaRequest;
import com.projeto.unify.services.AuthService;
import com.projeto.unify.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    /**
     * Endpoint para login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Endpoint para validar token
     */
    @PostMapping("/validar")
    public ResponseEntity<AuthResponse> validarToken(@RequestHeader("Authorization") String authHeader) {
        // O token vem como "Bearer <token>", precisamos remover o "Bearer "
        String token = authHeader.substring(7);
        return ResponseEntity.ok(authService.validarToken(token));
    }

    /**
     * Endpoint para trocar senha
     */
    @PostMapping("/trocar-senha")
    public ResponseEntity<?> trocarSenha(@RequestBody TrocarSenhaRequest request) {
        // Obtém o email do usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Chama o serviço para trocar a senha
        usuarioService.trocarSenha(email, request.getSenhaAtual(), request.getNovaSenha());

        return ResponseEntity.ok().build();
    }
}