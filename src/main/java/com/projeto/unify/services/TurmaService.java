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
    private final MateriaRepository materiaRepository;
    private final ProfessorRepository professorRepository;
    private final GraduacaoRepository graduacaoRepository;
    private final AlunoRepository alunoRepository; // For managing student enrollments

    private Funcionario getFuncionarioSecretariaLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);
        return funcionarioRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário da secretaria não encontrado ou não associado ao usuário logado."));
    }

    private Universidade getUniversidadeDoFuncionarioLogado() {
        Funcionario funcionarioLogado = getFuncionarioSecretariaLogado();
        Universidade universidade = funcionarioLogado.getUniversidade();
        if (universidade == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Funcionário não está associado a nenhuma universidade.");
        }
        return universidade;
    }

    @Transactional
    public Turma criar(TurmaDTO turmaDTO) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();

        Materia materia = materiaRepository.findById(turmaDTO.getMateriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matéria com ID " + turmaDTO.getMateriaId() + " não encontrada."));
        boolean materiaPertenceUniversidade = materia.getGraduacoes().stream().anyMatch(g -> g.getUniversidade().equals(universidade));
        if (!materiaPertenceUniversidade) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matéria não pertence à universidade do funcionário.");
        }

        Graduacao graduacao = graduacaoRepository.findById(turmaDTO.getGraduacaoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Graduação com ID " + turmaDTO.getGraduacaoId() + " não encontrada."));
        if (!graduacao.getUniversidade().equals(universidade)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Graduação não pertence à universidade do funcionário.");
        }
        // Ensure the materia is part of the selected graduacao
        if (!materia.getGraduacoes().contains(graduacao)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A matéria selecionada não pertence à graduação indicada.");
        }

        // Check for existing Turma with the same Materia, Graduacao, and Turno within the same university
        if (turmaRepository.existsByMateriaAndGraduacaoAndTurnoAndUniversidadeId(materia, graduacao, turmaDTO.getTurno(), universidade.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Já existe uma turma para esta Matéria, Graduação e Turno nesta universidade.");
        }

        Turma novaTurma = new Turma();
        novaTurma.setMateria(materia);
        novaTurma.setGraduacao(graduacao);
        novaTurma.setTurno(turmaDTO.getTurno().toUpperCase());
        novaTurma.setLimiteAlunos(turmaDTO.getLimiteAlunos());
        // universidade is implicitly set via Graduacao for Turma entity (Turma does not have direct Universidade field)

        if (turmaDTO.getProfessorId() != null) {
            Professor professor = professorRepository.findById(turmaDTO.getProfessorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor com ID " + turmaDTO.getProfessorId() + " não encontrado."));
            if (!professor.getUniversidade().equals(universidade)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Professor não pertence à universidade da turma.");
            }
            novaTurma.setProfessor(professor);
        }

        return turmaRepository.save(novaTurma);
    }

    @Transactional(readOnly = true)
    public List<Turma> listarTurmasDaUniversidadeLogada() {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        return turmaRepository.findByGraduacaoUniversidadeId(universidade.getId());
    }

    @Transactional(readOnly = true)
    public Turma buscarTurmaPorIdDaUniversidadeLogada(Long turmaId) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada com ID: " + turmaId));

        if (!turma.getGraduacao().getUniversidade().equals(universidade)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Turma não pertence à universidade do funcionário.");
        }
        return turma;
    }

    @Transactional
    public Turma atualizarTurma(Long turmaId, TurmaDTO turmaDTO) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Turma turmaExistente = buscarTurmaPorIdDaUniversidadeLogada(turmaId); // Validates ownership

        Materia materia = materiaRepository.findById(turmaDTO.getMateriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matéria com ID " + turmaDTO.getMateriaId() + " não encontrada."));
        boolean materiaPertenceUniversidade = materia.getGraduacoes().stream().anyMatch(g -> g.getUniversidade().equals(universidade));
        if (!materiaPertenceUniversidade) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matéria não pertence à universidade do funcionário.");
        }

        Graduacao graduacao = graduacaoRepository.findById(turmaDTO.getGraduacaoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Graduação com ID " + turmaDTO.getGraduacaoId() + " não encontrada."));
        if (!graduacao.getUniversidade().equals(universidade)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Graduação não pertence à universidade do funcionário.");
        }
        if (!materia.getGraduacoes().contains(graduacao)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A matéria selecionada não pertence à graduação indicada.");
        }

        // Check uniqueness if Materia, Graduacao, or Turno changed
        if (!turmaExistente.getMateria().equals(materia) || !turmaExistente.getGraduacao().equals(graduacao) || !turmaExistente.getTurno().equalsIgnoreCase(turmaDTO.getTurno())) {
            if (turmaRepository.existsByMateriaAndGraduacaoAndTurnoAndUniversidadeIdAndIdNot(materia, graduacao, turmaDTO.getTurno().toUpperCase(), universidade.getId(), turmaId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Já existe outra turma para esta Matéria, Graduação e Turno nesta universidade.");
            }
        }

        turmaExistente.setMateria(materia);
        turmaExistente.setGraduacao(graduacao);
        turmaExistente.setTurno(turmaDTO.getTurno().toUpperCase());
        turmaExistente.setLimiteAlunos(turmaDTO.getLimiteAlunos());

        if (turmaDTO.getProfessorId() != null) {
            if (turmaExistente.getProfessor() == null || !turmaDTO.getProfessorId().equals(turmaExistente.getProfessor().getId())) {
                Professor professor = professorRepository.findById(turmaDTO.getProfessorId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor com ID " + turmaDTO.getProfessorId() + " não encontrado."));
                if (!professor.getUniversidade().equals(universidade)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Professor não pertence à universidade da turma.");
                }
                turmaExistente.setProfessor(professor);
            }
        } else {
            turmaExistente.setProfessor(null); // Remove professor if ID is null
        }

        return turmaRepository.save(turmaExistente);
    }

    @Transactional
    public void deletarTurma(Long turmaId) {
        Turma turma = buscarTurmaPorIdDaUniversidadeLogada(turmaId); // Validates ownership

        if (!turma.getAlunos().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não é possível excluir a turma. Existem " + turma.getAlunos().size() + " aluno(s) matriculado(s).");
        }
        if (!turma.getAvaliacoesDefinidas().isEmpty()) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não é possível excluir a turma. Existem " + turma.getAvaliacoesDefinidas().size() + " avaliação(ões) definida(s) para esta turma.");
        }

        turmaRepository.delete(turma);
    }

    @Transactional
    public Turma matricularAluno(Long turmaId, Long alunoId) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Turma turma = buscarTurmaPorIdDaUniversidadeLogada(turmaId);

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno com ID " + alunoId + " não encontrado."));

        if (!aluno.getUniversidade().equals(universidade)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno não pertence à mesma universidade da turma.");
        }

        if (turma.getAlunos().size() >= turma.getLimiteAlunos()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Turma cheia. Não é possível matricular o aluno.");
        }

        if (aluno.getTurma() != null && aluno.getTurma().equals(turma)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno já está matriculado nesta turma.");
        }
        
        // If student is already in another class from the same graduation, it might be a transfer or an error.
        // For now, we allow changing class, which means the student will be removed from the previous class.
        if (aluno.getTurma() != null) {
            Turma turmaAntiga = aluno.getTurma();
            turmaAntiga.getAlunos().remove(aluno);
            // turmaRepository.save(turmaAntiga); // Not strictly necessary due to cascading if Aluno owns the relationship from its side, but good for clarity if Turma manages its list
        }

        aluno.setTurma(turma);
        turma.getAlunos().add(aluno); // Ensure bidirectional consistency if not handled by JPA automatically on Aluno side
        alunoRepository.save(aluno); // Save aluno to update its turma reference
        return turmaRepository.save(turma); // Save turma to update its alunos list
    }

    @Transactional
    public Turma removerAlunoDaTurma(Long turmaId, Long alunoId) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Turma turma = buscarTurmaPorIdDaUniversidadeLogada(turmaId);

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno com ID " + alunoId + " não encontrado."));

        if (!aluno.getUniversidade().equals(universidade)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno não pertence à mesma universidade da turma.");
        }

        if (aluno.getTurma() == null || !aluno.getTurma().equals(turma)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno não está matriculado nesta turma.");
        }

        aluno.setTurma(null);
        turma.getAlunos().remove(aluno);
        alunoRepository.save(aluno);
        return turmaRepository.save(turma);
    }
} 