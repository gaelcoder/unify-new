package com.projeto.unify.services;

import com.projeto.unify.models.Usuario;
import com.projeto.unify.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary  // Esta será a implementação padrão
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));

        // Verificar se o usuário está ativo
        if (!usuario.isAtivo()) {
            throw new DisabledException("Conta de usuário desativada");
        }

        List<SimpleGrantedAuthority> authorities = usuario.getPerfis().stream()
                .map(perfil -> new SimpleGrantedAuthority(perfil.getNome().name()))
                .collect(Collectors.toList());

        return new User(usuario.getEmail(), usuario.getSenha(), authorities);
    }
}
