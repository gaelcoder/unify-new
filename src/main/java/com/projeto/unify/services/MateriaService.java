package com.projeto.unify.services;

import com.projeto.unify.dtos.MateriaDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.repositories.FuncionarioRepository;
import com.projeto.unify.repositories.GraduacaoRepository;
import com.projeto.unify.repositories.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository materiaRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioService usuarioService;
    private final GraduacaoRepository graduacaoRepository;

    private Universidade getUniversidadeDoFuncionarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);
        Funcionario funcionario = funcionarioRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não encontrado para o usuário logado."));
        if (funcionario.getUniversidade() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Funcionário logado não está associado a nenhuma universidade.");
        }
        return funcionario.getUniversidade();
    }

    private Set<Graduacao> validarEProcessarGraduacoes(Set<Long> graduacaoIds, Universidade universidadeFuncionario) {
        if (graduacaoIds == null || graduacaoIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A matéria deve ser associada a pelo menos uma graduação.");
        }
        Set<Graduacao> graduacoesProcessadas = new HashSet<>();
        for (Long gradId : graduacaoIds) {
            Graduacao grad = graduacaoRepository.findById(gradId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Graduação com ID " + gradId + " não encontrada."));
            if (!grad.getUniversidade().equals(universidadeFuncionario)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Graduação " + grad.getTitulo() + " (ID: " + gradId + ") não pertence à sua universidade.");
            }
            graduacoesProcessadas.add(grad);
        }
        return graduacoesProcessadas;
    }

    @Transactional
    public Materia criar(MateriaDTO dto) {
        Universidade universidadeFuncionario = getUniversidadeDoFuncionarioLogado();
        Set<Graduacao> graduacoesAssociadas = validarEProcessarGraduacoes(dto.getGraduacaoIds(), universidadeFuncionario);

        // TODO: Add validation to ensure materia (titulo/codigo) is unique within the context (e.g., university or globally?)

        Materia materia = new Materia();
        materia.setTitulo(dto.getTitulo());
        materia.setCreditos(dto.getCreditos());
        materia.setCargaHoraria(dto.getCargaHoraria());
        materia.setCreditosNecessarios(dto.getCreditosNecessarios() != null ? dto.getCreditosNecessarios() : 0);
        materia.setGraduacoes(graduacoesAssociadas);

        return materiaRepository.save(materia);
    }

    public List<Materia> listarMateriasPorUniversidadeDoFuncionarioLogado() {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        // This will fetch materias that are linked to any graduacao from the secretary's university.
        // Duplicates might occur if a materia is linked to multiple graduations of the same university, hence distinct.
        return materiaRepository.findDistinctByGraduacoes_Universidade(universidade);
    }

    public Materia buscarMateriaPorIdEUniversidadeDoFuncionarioLogado(Long materiaId) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matéria não encontrada com o ID: " + materiaId));

        // Verify the materia is accessible by the funcionario (linked to at least one of their university's graduations)
        boolean accessible = materia.getGraduacoes().stream()
                .anyMatch(grad -> grad.getUniversidade().equals(universidade));
        if (!accessible) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Matéria não encontrada ou não pertence à sua universidade (ID: " + materiaId + ").");
        }
        return materia;
    }

    @Transactional
    public Materia atualizar(Long materiaId, MateriaDTO dto) {
        Universidade universidadeFuncionario = getUniversidadeDoFuncionarioLogado();
        Materia materia = buscarMateriaPorIdEUniversidadeDoFuncionarioLogado(materiaId); // Ensures initial access

        Set<Graduacao> novasGraduacoesAssociadas = validarEProcessarGraduacoes(dto.getGraduacaoIds(), universidadeFuncionario);

        // TODO: Add validation for uniqueness if titulo/codigo changes.

        materia.setTitulo(dto.getTitulo());
        materia.setCreditos(dto.getCreditos());
        materia.setCargaHoraria(dto.getCargaHoraria());
        materia.setCreditosNecessarios(dto.getCreditosNecessarios() != null ? dto.getCreditosNecessarios() : 0);
        materia.getGraduacoes().clear(); // Remove old associations
        materia.getGraduacoes().addAll(novasGraduacoesAssociadas); // Add new ones
        // Alternatively, for finer control or to preserve other collection types:
        // materia.setGraduacoes(novasGraduacoesAssociadas);

        return materiaRepository.save(materia);
    }

    @Transactional
    public void deletar(Long materiaId) {
        // Ensure the materia is accessible and deletable by this funcionario
        Materia materia = buscarMateriaPorIdEUniversidadeDoFuncionarioLogado(materiaId);

        // TODO: Add business logic: check if there are Turmas associated with this Materia before deleting.
        // For now, direct deletion. This will also clear entries from 'materia_graduacao' join table.
        if (!materia.getTurmas().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matéria não pode ser deletada pois possui turmas associadas. Remova as turmas primeiro.");
        }

        materiaRepository.delete(materia);
    }
} 