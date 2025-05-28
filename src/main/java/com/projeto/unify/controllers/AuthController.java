package com.projeto.unify.controllers;

import com.projeto.unify.dtos.TrocarSenhaRequest;
import com.projeto.unify.dtos.AuthResponse;
import com.projeto.unify.dtos.LoginRequest;
import com.projeto.unify.models.Perfil;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.security.JwtService;
import com.projeto.unify.services.AuthService;
import com.projeto.unify.services.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Autenticar usuário
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        // Gerar token JWT
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.gerarToken(userDetails);

        // Obter tipo do usuário (role)
        String tipo = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Obter informações adicionais do usuário
        Usuario usuario = usuarioService.findByEmail(request.getEmail());

        // Montar resposta
        AuthResponse authResponse = AuthResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .tipo(tipo)
                .token(token)
                .primeiroAcesso(usuario.isPrimeiroAcesso())
                .build();

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/primeiro-acesso")
    public ResponseEntity<?> alterarSenhaPrimeiroAcesso(@RequestBody TrocarSenhaRequest request) {
        usuarioService.atualizarSenha(request.getId(), request.getNovaSenha());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            // Chamar o serviço para invalidar o token
            authService.invalidarToken(jwt);
        }
        return ResponseEntity.ok().build();
    }

}