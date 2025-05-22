package com.projeto.unify.services;

import com.projeto.unify.dtos.AdministradorGeralDTO;
import com.projeto.unify.models.AdministradorGeral;
import com.projeto.unify.models.Perfil;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.repositories.AdministradorGeralRepository;
import com.projeto.unify.repositories.PerfilRepository;
import com.projeto.unify.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdministradorGeralService {

    private final AdministradorGeralRepository administradorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AdministradorGeral criarAdministrador(AdministradorGeralDTO dto) {
        // Verificar se email já existe
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado");
        }

        // Verificar se o perfil ADMIN_GERAL existe, senão criar
        Perfil perfilAdmin = perfilRepository.findByNome(Perfil.TipoPerfil.ROLE_ADMIN_GERAL)
                .orElseGet(() -> {
                    Perfil novoPerfil = new Perfil();
                    novoPerfil.setNome(Perfil.TipoPerfil.ROLE_ADMIN_GERAL);
                    return perfilRepository.save(novoPerfil);
                });

        // Criar usuário com senha criptografada
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.getPerfis().add(perfilAdmin);

        // Criar administrador
        AdministradorGeral admin = new AdministradorGeral();
        admin.setNome(dto.getNome());
        admin.setSobrenome(dto.getSobrenome());
        admin.setUsuario(usuario);

        return administradorRepository.save(admin);
    }

    public List<AdministradorGeral> listarTodos() {
        return administradorRepository.findAll();
    }

    public AdministradorGeral buscarPorId(Long id) {
        return administradorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Administrador não encontrado"));
    }

    @Transactional
    public void excluir(Long id) {
        AdministradorGeral admin = buscarPorId(id);
        administradorRepository.delete(admin);
    }
}