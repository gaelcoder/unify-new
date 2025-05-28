package com.projeto.unify.controllers;

import com.projeto.unify.dtos.TrocarSenhaRequest;
import com.projeto.unify.dtos.AuthResponse;
import com.projeto.unify.dtos.LoginRequest;
import com.projeto.unify.models.Perfil;
import com.projeto.unify.models.Representante;
import com.projeto.unify.models.Universidade;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.repositories.RepresentanteRepository;
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

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final RepresentanteRepository representanteRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.gerarToken(userDetails);

        String tipo = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Usuario usuario = usuarioService.findByEmail(request.getEmail());

        AuthResponse.AuthResponseBuilder authResponseBuilder = AuthResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .tipo(tipo)
                .token(token)
                .primeiroAcesso(usuario.isPrimeiroAcesso());

        if (tipo.contains(Perfil.TipoPerfil.ROLE_ADMIN_UNIVERSIDADE.name())) {
            Optional<Representante> representanteOpt = representanteRepository.findByUsuarioId(usuario.getId());
            if (representanteOpt.isPresent()) {
                Representante representante = representanteOpt.get();
                Universidade universidade = representante.getUniversidade();
                if (universidade != null) {
                    authResponseBuilder.universidadeId(universidade.getId());
                    authResponseBuilder.universidadeNome(universidade.getNome());
                    authResponseBuilder.universidadeLogoPath(universidade.getLogoPath());
                }
            }
        }

        return ResponseEntity.ok(authResponseBuilder.build());
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
            authService.invalidarToken(jwt);
        }
        return ResponseEntity.ok().build();
    }

}