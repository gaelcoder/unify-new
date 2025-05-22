package com.projeto.unify.services;

import com.projeto.unify.dtos.UniversidadeDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UniversidadeService {

    private final UniversidadeRepository universidadeRepository;
    private final RepresentanteRepository representanteRepository; // Modificado para final

    public Universidade criar(UniversidadeDTO dto) {
        Universidade universidade = new Universidade(
                dto.getNome(),
                dto.getCnpj(),
                dto.getFundacao(),
                dto.getSigla(),
                null,
                dto.getCampus()
        );

        universidade = universidadeRepository.save(universidade);

        if (dto.getRepresentanteId() != null) {
            Representante representante = representanteRepository.findById(dto.getRepresentanteId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Representante não encontrado"));

            universidade.setRepresentante(representante);
            representante.setUniversidade(universidade);

            representanteRepository.save(representante);
            universidade = universidadeRepository.save(universidade);
        }



        return universidadeRepository.save(universidade);
    }

    public List<Universidade> listarTodos() {
        return universidadeRepository.findAll();
    }

    public Universidade buscarPorId(Long id) {
        return universidadeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Universidade não encontrada"));
    }

    public Universidade atualizar(Long id, UniversidadeDTO dto) {
        Universidade universidade = buscarPorId(id);

        universidade.setNome(dto.getNome());
        // Não atualizar CNPJ
        universidade.setFundacao(dto.getFundacao());
        universidade.setSigla(dto.getSigla());
        universidade.setCampus(dto.getCampus());

        return universidadeRepository.save(universidade);
    }

    @Transactional
    public Universidade associarRepresentante(Long universidadeId, Long representanteId) {
        Universidade universidade = buscarPorId(universidadeId);
        Representante representante = representanteRepository.findById(representanteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Representante não encontrado"));

        // Verificar e limpar associações existentes para evitar conflitos
        if (universidade.getRepresentante() != null) {
            Representante antigoRepresentante = universidade.getRepresentante();
            antigoRepresentante.setUniversidade(null);
            universidade.setRepresentante(null);
            representanteRepository.save(antigoRepresentante);
            universidadeRepository.save(universidade);
        }

        if (representante.getUniversidade() != null) {
            Universidade antigaUniversidade = representante.getUniversidade();
            antigaUniversidade.setRepresentante(null);
            representante.setUniversidade(null);
            universidadeRepository.save(antigaUniversidade);
            representanteRepository.save(representante);
        }

        // Criar a nova associação
        universidade.setRepresentante(representante);
        representante.setUniversidade(universidade);

        representanteRepository.save(representante);
        return universidadeRepository.save(universidade);
    }

    @Transactional
    public Universidade desassociarRepresentante(Long universidadeId) {
        Universidade universidade = buscarPorId(universidadeId);

        if (universidade.getRepresentante() != null) {
            Representante representante = universidade.getRepresentante();
            representante.setUniversidade(null);
            universidade.setRepresentante(null);
            representanteRepository.save(representante);
        }

        return universidadeRepository.save(universidade);
    }

    @Transactional
    public void excluir(Long id) {
        Universidade universidade = buscarPorId(id);

        // Se tiver um representante associado, desassociar primeiro
        if (universidade.getRepresentante() != null) {
            Representante representante = universidade.getRepresentante();
            representante.setUniversidade(null);
            universidade.setRepresentante(null);
            representanteRepository.save(representante);
        }

        universidadeRepository.delete(universidade);
    }
}