package com.projeto.unify.services;

import com.projeto.unify.dtos.UniversidadeDTO;
import com.projeto.unify.models.Universidade;
import com.projeto.unify.repositories.UniversidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UniversidadeService {

    private final UniversidadeRepository universidadeRepository;

    public Universidade criar(UniversidadeDTO dto) {
        Universidade universidade = new Universidade(
                dto.getNome(),
                dto.getCnpj(),
                dto.getFundacao(),
                dto.getSigla(),
                null,
                dto.getCampus()
        );
        return universidadeRepository.save(universidade);
    }

    public List<Universidade> listarTodos() {
        return universidadeRepository.findAll();
    }
}
