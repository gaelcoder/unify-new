package com.projeto.unify.services;

import com.projeto.unify.dtos.RepresentanteDTO;
import com.projeto.unify.models.Representante;
import com.projeto.unify.repositories.RepresentanteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepresentanteService {

    private final RepresentanteRepository representanteRepository;

    public Representante criar(RepresentanteDTO dto) {
        Representante representante = new Representante(
                dto.getCpf(),
                dto.getDataNascimento(),
                dto.getNome(),
                dto.getSobrenome(),
                dto.getEmail(),
                dto.getTelefone(),
                dto.getCargo()
        );
        return representanteRepository.save(representante);
    }
}
