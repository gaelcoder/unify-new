package com.projeto.unify.services;

import com.projeto.unify.models.Perfil;
import com.projeto.unify.repositories.PerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PerfilService {

    @Autowired
    private PerfilRepository perfilRepository;

    public Perfil obterOuCriarPerfil(Perfil.TipoPerfil tipoPerfil) {
        return perfilRepository.findByNome(tipoPerfil)
                .orElseGet(() -> {
                    Perfil novoPerfil = new Perfil(tipoPerfil);
                    return perfilRepository.save(novoPerfil);
                });
    }
}