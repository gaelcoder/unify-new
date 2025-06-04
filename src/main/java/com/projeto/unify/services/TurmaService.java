package com.projeto.unify.services;

import com.projeto.unify.dtos.TurmaDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioService usuarioService;
    private final GraduacaoRepository graduacaoRepository;
    private final MateriaRepository materiaRepository;
    private final ProfessorRepository professorRepository;

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

    @Transactional
    public Turma criar(TurmaDTO dto) {
        Universidade uniFuncionario = getUniversidadeDoFuncionarioLogado();

        Graduacao graduacao = graduacaoRepository.findById(dto.getGraduacaoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Graduação com ID " + dto.getGraduacaoId() + " não encontrada."));
        if (!graduacao.getUniversidade().equals(uniFuncionario)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Graduação especificada não pertence à sua universidade.");
        }

        Materia materia = materiaRepository.findById(dto.getMateriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matéria com ID " + dto.getMateriaId() + " não encontrada."));
        // Ensure the Materia is part of the chosen Graduacao
        if (materia.getGraduacoes() == null || !materia.getGraduacoes().contains(graduacao)) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matéria " + materia.getTitulo() + " não está associada à graduação " + graduacao.getTitulo() + ".");
        }

        Professor professor = null;
        if (dto.getProfessorId() != null) {
            professor = professorRepository.findById(dto.getProfessorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor com ID " + dto.getProfessorId() + " não encontrado."));
            if (!professor.getUniversidade().equals(uniFuncionario)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Professor especificado não pertence à sua universidade.");
            }
        }

        // TODO: Add validation for unique Turma (e.g., based on materia, graduacao, turno, ano/semestre - if those fields are added)

        Turma turma = new Turma();
        turma.setGraduacao(graduacao);
        turma.setMateria(materia);
        turma.setProfessor(professor);
        turma.setTurno(dto.getTurno().toUpperCase());
        turma.setLimiteAlunos(dto.getLimiteAlunos());

        return turmaRepository.save(turma);
    }

    public List<Turma> listarTurmasPorUniversidadeDoFuncionarioLogado() {
        Universidade uniFuncionario = getUniversidadeDoFuncionarioLogado();
        return turmaRepository.findByGraduacao_Universidade(uniFuncionario);
    }

    public Turma buscarTurmaPorIdEUniversidadeDoFuncionarioLogado(Long turmaId) {
        Universidade uniFuncionario = getUniversidadeDoFuncionarioLogado();
        return turmaRepository.findByIdAndGraduacao_Universidade(turmaId, uniFuncionario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada ou não pertence à sua universidade."));
    }

    @Transactional
    public Turma atualizar(Long turmaId, TurmaDTO dto) {
        Universidade uniFuncionario = getUniversidadeDoFuncionarioLogado();
        Turma turma = turmaRepository.findByIdAndGraduacao_Universidade(turmaId, uniFuncionario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada ou não pertence à sua universidade para atualização."));

        // Validate Graduacao for the turma (usually fixed, but if DTO allows change, re-validate)
        Graduacao graduacao = graduacaoRepository.findById(dto.getGraduacaoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Graduação com ID " + dto.getGraduacaoId() + " não encontrada."));
        if (!graduacao.getUniversidade().equals(uniFuncionario)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova graduação especificada não pertence à sua universidade.");
        }
        // If graduacao itself changes, it might have implications for existing students - not handled here.

        Materia materia = materiaRepository.findById(dto.getMateriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matéria com ID " + dto.getMateriaId() + " não encontrada."));
        if (materia.getGraduacoes() == null || !materia.getGraduacoes().contains(graduacao)) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova matéria " + materia.getTitulo() + " não está associada à graduação " + graduacao.getTitulo() + ".");
        }

        Professor professor = null;
        if (dto.getProfessorId() != null) {
            professor = professorRepository.findById(dto.getProfessorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor com ID " + dto.getProfessorId() + " não encontrado."));
            if (!professor.getUniversidade().equals(uniFuncionario)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Novo professor especificado não pertence à sua universidade.");
            }
        }

        turma.setGraduacao(graduacao);
        turma.setMateria(materia);
        turma.setProfessor(professor);
        turma.setTurno(dto.getTurno().toUpperCase());
        turma.setLimiteAlunos(dto.getLimiteAlunos());

        return turmaRepository.save(turma);
    }

    @Transactional
    public void deletar(Long turmaId) {
        Turma turma = buscarTurmaPorIdEUniversidadeDoFuncionarioLogado(turmaId); // Ensures it belongs to the secretary's university

        if (!turma.getAlunos().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Turma não pode ser deletada pois possui alunos matriculados.");
        }
        // Add other checks if needed, e.g., active period.

        turmaRepository.delete(turma);
    }
} 