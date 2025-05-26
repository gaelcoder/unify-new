package com.projeto.unify.services;

import com.projeto.unify.models.Perfil;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.repositories.PerfilRepository;
import com.projeto.unify.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));

        List<SimpleGrantedAuthority> authorities = usuario.getPerfis().stream()
                .map(perfil -> new SimpleGrantedAuthority(perfil.getNome().name()))
                .collect(Collectors.toList());

        return new User(usuario.getEmail(), usuario.getSenha(), authorities);
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public void atualizarSenha(Long userId, String novaSenha) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setPrimeiroAcesso(false);

        usuarioRepository.save(usuario);
    }

    @Transactional
    public Perfil obterOuCriarPerfil(Perfil.TipoPerfil tipoPerfil) {
        Optional<Perfil> perfilExistente = perfilRepository.findByNome(tipoPerfil);

        return perfilExistente.orElseGet(() -> {
            Perfil novoPerfil = new Perfil(tipoPerfil);
            return perfilRepository.save(novoPerfil);
        });
    }

    public void criarAdminGeral(String adminEmail, String adminSenha, boolean b) {
        Usuario admin = new Usuario();
        admin.setEmail(adminEmail);
        admin.setSenha(passwordEncoder.encode(adminSenha));
    }

    public boolean existsByEmail(String adminEmail) {
        return usuarioRepository.existsByEmail(adminEmail);
    }
}