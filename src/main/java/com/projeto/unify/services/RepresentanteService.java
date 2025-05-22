package com.projeto.unify.services;

import com.projeto.unify.dtos.RepresentanteDTO;
import com.projeto.unify.models.Representante;
import com.projeto.unify.models.Universidade;
import com.projeto.unify.repositories.RepresentanteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepresentanteService {

    private final UsuarioService usuarioService;
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

    public Representante atualizar(Long id, RepresentanteDTO dto) {
        Representante representante = buscarPorId(id);

        representante.setNome(dto.getNome());
        representante.setSobrenome(dto.getSobrenome());
        representante.setEmail(dto.getEmail());
        representante.setTelefone(dto.getTelefone());
        representante.setCargo(dto.getCargo());

        return representanteRepository.save(representante);
    }


    public List<Representante> listarTodos() {
        return representanteRepository.findAll();
    }

    public List<Representante> listarDisponiveis() {
        return representanteRepository.findAll().stream()
                .filter(r -> r.getUniversidade() == null)
                .collect(Collectors.toList());
    }

    public Representante buscarPorId(Long id) {
        return representanteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Representante não encontrado"));
    }

    public void excluir(Long id) {
        Representante representante = buscarPorId(id);

        // Se estiver associado a uma universidade, desassociar primeiro
        if (representante.getUniversidade() != null) {
            representante.getUniversidade().setRepresentante(null);
            representante.setUniversidade(null);
        }

        representanteRepository.delete(representante);
    }

    @Transactional
    public void associarAUniversidade(Long representanteId, Universidade universidade) {
        Representante representante = buscarPorId(representanteId);

        // Verificar se o representante já está associado
        if (representante.getUniversidade() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Representante já está associado a uma universidade");
        }

        // Associar representante à universidade
        representante.setUniversidade(universidade);

        // Criar usuário administrador para o representante
        usuarioService.criarAdminUniversidade(representante, universidade);

        representanteRepository.save(representante);
    }


}