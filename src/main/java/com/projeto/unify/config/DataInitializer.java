package com.projeto.unify.config;

import com.projeto.unify.models.Perfil;
import com.projeto.unify.repositories.PerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PerfilRepository perfilRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Inicializar todos os tipos de perfil
            for (Perfil.TipoPerfil tipo : Perfil.TipoPerfil.values()) {
                if (perfilRepository.findByNome(tipo).isEmpty()) {
                    Perfil perfil = new Perfil(tipo);
                    perfilRepository.save(perfil);
                    System.out.println("Perfil criado: " + tipo);
                }
            }
        };
    }
}