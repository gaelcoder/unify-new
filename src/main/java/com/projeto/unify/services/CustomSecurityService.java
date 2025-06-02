package com.projeto.unify.services;

import com.projeto.unify.models.Aluno;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.repositories.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service("customSecurityService") // Bean name to be referenced in @PreAuthorize
@RequiredArgsConstructor
public class CustomSecurityService {

    private final AlunoRepository alunoRepository;
    private final UsuarioService usuarioService; // To get current Usuario

    public boolean isAlunoLogadoEProprioDonoDosDados(Long alunoIdTarget) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return false; // Not authenticated
        }

        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);
        if (usuarioLogado == null) {
            return false; // Should not happen if authenticated
        }

        Aluno alunoLogado = alunoRepository.findByUsuarioId(usuarioLogado.getId())
            .orElse(null);

        if (alunoLogado == null) {
            return false; // Logged-in user is not an Aluno
        }

        // Check if the logged-in Aluno's ID matches the target Aluno ID
        return alunoLogado.getId().equals(alunoIdTarget);
    }

    // You can add more custom security methods here, e.g., for professor access to their turmas, etc.
} 