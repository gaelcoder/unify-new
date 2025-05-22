package com.projeto.unify.config;

import com.projeto.unify.dtos.AdministradorGeralDTO;
import com.projeto.unify.models.Perfil;
import com.projeto.unify.repositories.PerfilRepository;
import com.projeto.unify.repositories.UsuarioRepository;
import com.projeto.unify.services.AdministradorGeralService;
import com.projeto.unify.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioService usuarioService;

    @Value("${unify.admin.email:admin@unify.com}")
    private String adminEmail;

    @Value("${unify.admin.senha:admin123}")
    private String adminSenha;

    @Override
    public void run(String... args) throws Exception {
        // Verificar se já existe administrador geral
        try {
            // Criar admin geral apenas se ainda não existir
            usuarioService.criarAdminGeral(adminEmail, adminSenha, false);
            System.out.println("Administrador geral criado com sucesso!");
            System.out.println("Email: " + adminEmail);
            System.out.println("Senha: " + adminSenha);
        } catch (Exception e) {
            // Admin já existe, ignorar
            System.out.println("Admin geral já existe ou ocorreu um erro: " + e.getMessage());
        }
    }
}
