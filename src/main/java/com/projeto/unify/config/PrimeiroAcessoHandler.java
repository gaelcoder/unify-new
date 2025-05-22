package com.projeto.unify.config;

import com.projeto.unify.models.Usuario;
import com.projeto.unify.repositories.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PrimeiroAcessoHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();

        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (usuario.isPrimeiroAcesso()) {
            // Redirecionar para página de troca de senha
            redirectStrategy.sendRedirect(request, response, "/trocar-senha");
        } else {
            // Redirecionar para dashboard
            redirectStrategy.sendRedirect(request, response, "/dashboard");
        }
    }
}
