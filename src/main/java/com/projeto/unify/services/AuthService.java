package com.projeto.unify.services;

import com.projeto.unify.dtos.AuthResponse;
import com.projeto.unify.dtos.LoginRequest;
import com.projeto.unify.models.Perfil;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Autentica um usuário e gera um token JWT
     */
    public AuthResponse login(LoginRequest request) {
        try {
            // Tenta autenticar o usuário
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getSenha()
                    )
            );

            // Define o contexto de segurança
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Busca o usuário do banco de dados
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

            // Gera o token JWT
            String jwt = jwtService.generateToken(usuario);

            // Determina o tipo de usuário
            String tipo = "USUARIO";
            if (!usuario.getPerfis().isEmpty()) {
                tipo = usuario.getPerfis().iterator().next().getNome().name();
            }

            // Constrói e retorna a resposta
            return AuthResponse.builder()
                    .token(jwt)
                    .email(usuario.getEmail())
                    .tipo(tipo)
                    .primeiroAcesso(usuario.isPrimeiroAcesso())
                    .mensagem("Login realizado com sucesso")
                    .build();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Credenciais inválidas ou usuário não encontrado");
        }
    }

    /**
     * Valida o token JWT e retorna as informações do usuário
     */
    public AuthResponse validarToken(String token) {
        try {
            // Extrai o email do token
            String email = jwtService.extractUsername(token);

            // Busca o usuário do banco de dados
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

            // Verifica se o token é válido
            if (jwtService.isTokenValid(token, usuario)) {
                // Determina o tipo de usuário
                String tipo = "USUARIO";
                if (!usuario.getPerfis().isEmpty()) {
                    tipo = usuario.getPerfis().iterator().next().getNome().name();
                }

                // Constrói e retorna a resposta
                return AuthResponse.builder()
                        .token(token) // Retorna o mesmo token
                        .email(usuario.getEmail())
                        .tipo(tipo)
                        .primeiroAcesso(usuario.isPrimeiroAcesso())
                        .mensagem("Token válido")
                        .build();
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido ou expirado");
            }

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido ou expirado");
        }
    }
}