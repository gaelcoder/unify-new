package com.projeto.unify.config;

import com.projeto.unify.models.AdministradorGeral;
import com.projeto.unify.models.Perfil;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.repositories.AdministradorGeralRepository;
import com.projeto.unify.repositories.PerfilRepository;
import com.projeto.unify.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final AdministradorGeralRepository administradorGeralRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${unify.admin.email}")
    private String adminEmail;

    @Value("${unify.admin.senha}")
    private String adminSenha;

    @Value("${unify.admin.nome}")
    private String adminNome;

    @Value("${unify.admin.sobrenome}")
    private String adminSobrenome;

    @Override
    @Transactional
    public void run(String... args) {
        initializeAdmin();
    }

    private void initializeAdmin() {
        // Verificar se o admin já existe
        if (usuarioRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }

        // Criar ou obter o perfil de admin
        Perfil perfilAdmin = perfilRepository.findByNome(Perfil.TipoPerfil.ROLE_ADMIN_GERAL)
                .orElseGet(() -> {
                    Perfil novoPerfil = new Perfil(Perfil.TipoPerfil.ROLE_ADMIN_GERAL);
                    return perfilRepository.save(novoPerfil);
                });

        // Criar usuário admin
        Usuario adminUser = new Usuario();
        adminUser.setEmail(adminEmail);
        adminUser.setNome(adminNome);
        adminUser.setSenha(passwordEncoder.encode(adminSenha));
        adminUser.setPrimeiroAcesso(false);
        adminUser.setAtivo(true);
        adminUser.adicionarPerfil(perfilAdmin);
        
        // Salvar usuário
        Usuario savedUser = usuarioRepository.save(adminUser);

        // Criar administrador geral
        AdministradorGeral admin = new AdministradorGeral();
        admin.setNome(adminNome);
        admin.setSobrenome(adminSobrenome);
        admin.setUsuario(savedUser);
        
        // Salvar administrador
        administradorGeralRepository.save(admin);
    }
}