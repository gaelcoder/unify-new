package com.projeto.unify.config;

import com.projeto.unify.models.AdministradorGeral;
import com.projeto.unify.models.Perfil;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.repositories.AdministradorGeralRepository;
import com.projeto.unify.repositories.PerfilRepository;
import com.projeto.unify.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
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

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            // Criar perfis se não existirem
            for (Perfil.TipoPerfil tipo : Perfil.TipoPerfil.values()) {
                if (perfilRepository.findByNome(tipo).isEmpty()) {
                    Perfil perfil = new Perfil(tipo);
                    perfilRepository.save(perfil);
                }
            }

            // Criar admin se não existir
            if (usuarioRepository.findByEmail(adminEmail).isEmpty()) {
                Usuario adminUser = new Usuario();
                adminUser.setEmail(adminEmail);
                adminUser.setSenha(passwordEncoder.encode(adminSenha));
                adminUser.setNome(adminNome);
                adminUser.setPrimeiroAcesso(false);
                adminUser.setAtivo(true);

                // Adicionar perfil de admin
                perfilRepository.findByNome(Perfil.TipoPerfil.ROLE_ADMIN_GERAL)
                        .ifPresent(adminUser::adicionarPerfil);

                usuarioRepository.save(adminUser);

                // Criar entidade AdministradorGeral
                AdministradorGeral admin = new AdministradorGeral();
                admin.setNome(adminNome);
                admin.setSobrenome(adminSobrenome);
                admin.setUsuario(adminUser);
                administradorGeralRepository.save(admin);
            }
        };
    }
}