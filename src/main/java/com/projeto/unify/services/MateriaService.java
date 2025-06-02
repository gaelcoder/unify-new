package com.projeto.unify.services;

import com.projeto.unify.dtos.MateriaDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.repositories.*;
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
    private final UsuarioService usuarioService; // Para pegar usuário logado
    private final GraduacaoRepository graduacaoRepository; // Para buscar graduações por ID

    private Funcionario getFuncionarioSecretariaLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);
        return funcionarioRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário da secretaria não encontrado."));
    }

    @Transactional
    public Materia criar(MateriaDTO materiaDTO) {
        Funcionario funcionarioLogado = getFuncionarioSecretariaLogado();
        Universidade universidadeFuncionario = funcionarioLogado.getUniversidade();
        if (universidadeFuncionario == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Funcionário não está associado a nenhuma universidade.");
        }

        // Valida se já existe matéria com mesmo título na universidade
        if (materiaRepository.existsByTituloAndUniversidade(materiaDTO.getTitulo(), universidadeFuncionario.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Já existe uma matéria com o título '" + materiaDTO.getTitulo() + "' nesta universidade.");
        }

        Set<Graduacao> graduacoesParaAssociar = new HashSet<>();
        if (materiaDTO.getGraduacaoIds() != null && !materiaDTO.getGraduacaoIds().isEmpty()) {
            for (Long gradId : materiaDTO.getGraduacaoIds()) {
                Graduacao grad = graduacaoRepository.findById(gradId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Graduação com ID " + gradId + " não encontrada."));
                // Valida se a graduação pertence à mesma universidade do funcionário
                if (!grad.getUniversidade().equals(universidadeFuncionario)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                        "Graduação '" + grad.getTitulo() + "' (ID: " + gradId + ") não pertence à universidade do funcionário.");
                }
                graduacoesParaAssociar.add(grad);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A matéria deve ser associada a pelo menos uma graduação.");
        }

        Materia novaMateria = new Materia();
        novaMateria.setTitulo(materiaDTO.getTitulo());
        novaMateria.setCreditos(materiaDTO.getCreditos());
        novaMateria.setCargaHoraria(materiaDTO.getCargaHoraria());
        novaMateria.setCreditosNecessarios(materiaDTO.getCreditosNecessarios() != null ? materiaDTO.getCreditosNecessarios() : 0);
        novaMateria.setNotaMinimaAprovacao(materiaDTO.getNotaMinimaAprovacao());
        novaMateria.setGraduacoes(graduacoesParaAssociar);
        // A lista de Turmas é inicializada vazia por padrão e gerenciada em TurmaService

        return materiaRepository.save(novaMateria);
    }

    @Transactional(readOnly = true)
    public List<Materia> listarMateriasDaUniversidadeLogada() {
        Funcionario funcionarioLogado = getFuncionarioSecretariaLogado();
        Universidade universidadeFuncionario = funcionarioLogado.getUniversidade();
        if (universidadeFuncionario == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Funcionário não está associado a nenhuma universidade.");
        }
        // Lista matérias que estão associadas a graduações da universidade do funcionário
        return materiaRepository.findByUniversidadeId(universidadeFuncionario.getId());
    }

    @Transactional(readOnly = true)
    public Materia buscarMateriaPorIdDaUniversidadeLogada(Long materiaId) {
        Funcionario funcionarioLogado = getFuncionarioSecretariaLogado();
        Universidade universidadeFuncionario = funcionarioLogado.getUniversidade();
         if (universidadeFuncionario == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Funcionário não está associado a nenhuma universidade.");
        }

        Materia materia = materiaRepository.findById(materiaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matéria não encontrada com ID: " + materiaId));

        // Valida se a matéria pertence à universidade do funcionário (verificando suas graduações)
        boolean pertenceUniversidade = materia.getGraduacoes().stream()
            .anyMatch(grad -> grad.getUniversidade().equals(universidadeFuncionario));
        if (!pertenceUniversidade) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Matéria não pertence à universidade do funcionário.");
        }
        return materia;
    }

    @Transactional
    public Materia atualizarMateria(Long materiaId, MateriaDTO materiaDTO) {
        Funcionario funcionarioLogado = getFuncionarioSecretariaLogado();
        Universidade universidadeFuncionario = funcionarioLogado.getUniversidade();
        if (universidadeFuncionario == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Funcionário não está associado a nenhuma universidade.");
        }

        Materia materiaExistente = buscarMateriaPorIdDaUniversidadeLogada(materiaId); // Já valida a permissão

        // Valida título apenas se mudou e se já existe outra matéria com o novo título na universidade
        if (!materiaExistente.getTitulo().equals(materiaDTO.getTitulo())) {
            if (materiaRepository.existsByTituloAndUniversidadeAndIdNot(materiaDTO.getTitulo(), universidadeFuncionario.getId(), materiaId)) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Já existe outra matéria com o título '" + materiaDTO.getTitulo() + "' nesta universidade.");
            }
            materiaExistente.setTitulo(materiaDTO.getTitulo());
        }

        Set<Graduacao> graduacoesParaAssociar = new HashSet<>();
        if (materiaDTO.getGraduacaoIds() != null && !materiaDTO.getGraduacaoIds().isEmpty()) {
            for (Long gradId : materiaDTO.getGraduacaoIds()) {
                Graduacao grad = graduacaoRepository.findById(gradId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Graduação com ID " + gradId + " não encontrada."));
                if (!grad.getUniversidade().equals(universidadeFuncionario)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                        "Graduação '" + grad.getTitulo() + "' (ID: " + gradId + ") não pertence à universidade do funcionário.");
                }
                graduacoesParaAssociar.add(grad);
            }
        } else {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A matéria deve ser associada a pelo menos uma graduação.");
        }

        materiaExistente.setCreditos(materiaDTO.getCreditos());
        materiaExistente.setCargaHoraria(materiaDTO.getCargaHoraria());
        materiaExistente.setCreditosNecessarios(materiaDTO.getCreditosNecessarios() != null ? materiaDTO.getCreditosNecessarios() : 0);
        materiaExistente.setNotaMinimaAprovacao(materiaDTO.getNotaMinimaAprovacao());
        materiaExistente.setGraduacoes(graduacoesParaAssociar);

        return materiaRepository.save(materiaExistente);
    }

    @Transactional
    public void deletarMateria(Long materiaId) {
        Funcionario funcionarioLogado = getFuncionarioSecretariaLogado(); // Garante que é um funcionário da secretaria
        Universidade universidadeFuncionario = funcionarioLogado.getUniversidade();
        if (universidadeFuncionario == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Funcionário não está associado a nenhuma universidade.");
        }

        Materia materia = buscarMateriaPorIdDaUniversidadeLogada(materiaId); // Valida se matéria existe e pertence à universidade

        // Antes de deletar, verificar se a matéria está sendo usada em alguma Turma
        // Se sim, a exclusão pode ser impedida ou exigir outras ações (ex: desassociar turmas)
        if (materia.getTurmas() != null && !materia.getTurmas().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Não é possível excluir a matéria pois ela está associada a " + materia.getTurmas().size() + " turma(s). Remova-a das turmas primeiro.");
        }

        materiaRepository.delete(materia);
    }
} 