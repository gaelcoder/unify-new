package com.projeto.unify.config;

import com.projeto.unify.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    private final UsuarioService usuarioService;

    @Value("${unify.admin.email}")
    private String adminEmail;

    @Value("${unify.admin.senha}")
    private String adminSenha;

    @Override
    public void run(String... args) {
        try {
            logger.info("Verificando existência do admin geral...");

            // Verificar se o admin já existe pelo email
            if (!usuarioService.existsByEmail(adminEmail)) {
                logger.info("Admin geral não encontrado, criando...");
                usuarioService.criarAdminGeral(adminEmail, adminSenha, false);
                logger.info("Admin geral criado com sucesso: {}", adminEmail);
            } else {
                logger.info("Admin geral já existe: {}", adminEmail);
            }
        } catch (Exception e) {
            logger.error("Erro ao inicializar admin geral: {}", e.getMessage(), e);
        }
    }
}