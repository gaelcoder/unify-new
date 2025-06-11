package com.projeto.unify.services;

import com.projeto.unify.dtos.TurmaCreateDTO;
import com.projeto.unify.dtos.TurmaDTO;
import com.projeto.unify.dtos.TurmaUpdateDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioService usuarioService;
    private final MateriaRepository materiaRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;

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

    private String sanitizeDiaSemana(String diaSemana) {
        if (diaSemana == null) return null;
        return diaSemana.trim().toUpperCase().replace("-FEIRA", "");
    }

    private String sanitizeTurno(String turno) {
        if (turno == null) return null;
        return turno.trim().toUpperCase();
    }

    @Transactional
    public Turma create(TurmaCreateDTO dto) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();

        Professor professor = professorRepository.findById(dto.getProfessorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado."));
        if (!Objects.equals(professor.getUniversidade().getId(), universidade.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Professor não pertence à sua universidade.");
        }

        String turnoSanitized = sanitizeTurno(dto.getTurno());
        String diaSemanaSanitized = sanitizeDiaSemana(dto.getDiaSemana());

        boolean professorJaTemTurmaNoTurno = turmaRepository.existsByProfessorAndTurnoAndDiaSemana(professor, turnoSanitized, diaSemanaSanitized);
        if (professorJaTemTurmaNoTurno) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Professor " + professor.getNome() + " já leciona uma turma no turno da " + dto.getTurno() + " na " + dto.getDiaSemana() + ".");
        }

        Materia materia = materiaRepository.findById(dto.getMateriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matéria não encontrada."));

        if (!universidade.getCampus().contains(dto.getCampus())) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O campus '" + dto.getCampus() + "' não pertence a esta universidade.");
        }

        Turma turma = new Turma();
        turma.setProfessor(professor);
        turma.setMateria(materia);
        turma.setTurno(turnoSanitized);
        turma.setDiaSemana(diaSemanaSanitized);
        turma.setCampus(dto.getCampus());
        turma.setLimiteAlunos(dto.getLimiteAlunos());

        if (dto.getAlunoIds() != null && !dto.getAlunoIds().isEmpty()) {
            if (dto.getAlunoIds().size() > dto.getLimiteAlunos()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Número de alunos excede o limite da turma.");
            }
            List<Aluno> alunos = alunoRepository.findAllById(dto.getAlunoIds());
            for (Aluno aluno : alunos) {
                boolean alunoJaTemTurmaNoTurno = turmaRepository.existsByAlunosInAndTurnoAndDiaSemana(List.of(aluno), turnoSanitized, diaSemanaSanitized);
                if (alunoJaTemTurmaNoTurno) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "O aluno " + aluno.getNome() + " já está em outra turma na " + dto.getDiaSemana() + " no turno da " + dto.getTurno() + ".");
                }
                if (!aluno.getUniversidade().getId().equals(universidade.getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno " + aluno.getNome() + " (ID: " + aluno.getId() + ") não pertence à sua universidade.");
                }
            }
            turma.setAlunos(alunos);
        } else {
            turma.setAlunos(new ArrayList<>());
        }

        return turmaRepository.save(turma);
    }

    @Transactional
    public TurmaDTO update(Long turmaId, TurmaUpdateDTO dto) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Turma turma = this.findByIdAndLoggedInUserUniversity(turmaId);

        if (dto.getProfessorId() != null && !dto.getProfessorId().equals(turma.getProfessor().getId())) {
            Professor novoProfessor = professorRepository.findById(dto.getProfessorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado."));
            if (!Objects.equals(novoProfessor.getUniversidade().getId(), universidade.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Professor não pertence à sua universidade.");
            }
            boolean professorJaTemTurmaNoTurno = turmaRepository.existsByProfessorAndTurnoAndDiaSemana(novoProfessor, turma.getTurno(), turma.getDiaSemana());
            if (professorJaTemTurmaNoTurno) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Professor " + novoProfessor.getNome() + " já leciona outra turma no turno da " + turma.getTurno() + " na " + turma.getDiaSemana() + ".");
            }
            turma.setProfessor(novoProfessor);
        }

        if (dto.getAlunoIds() != null) {
            if (dto.getAlunoIds().size() > turma.getLimiteAlunos()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Número de alunos excede o limite da turma.");
            }
            List<Aluno> novosAlunos = alunoRepository.findAllById(dto.getAlunoIds());
            for (Aluno aluno : novosAlunos) {
                boolean alunoJaTemTurmaNoTurno = turmaRepository.existsByAlunosInAndTurnoAndDiaSemanaAndIdNot(List.of(aluno), turma.getTurno(), turma.getDiaSemana(), turmaId);
                if (alunoJaTemTurmaNoTurno) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "O aluno " + aluno.getNome() + " já está em outra turma na " + turma.getDiaSemana() + " no turno da " + turma.getTurno() + ".");
                }
                if (!aluno.getUniversidade().getId().equals(universidade.getId())) {
                     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno " + aluno.getNome() + " (ID: " + aluno.getId() + ") não pertence à sua universidade.");
                }
            }
            turma.setAlunos(new ArrayList<>(novosAlunos));
        }

        Turma turmaAtualizada = turmaRepository.save(turma);
        return new TurmaDTO(turmaAtualizada);
    }

    @Transactional(readOnly = true)
    public List<Aluno> findEligibleStudents(String campus, Long materiaId, String diaSemana, String turno) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matéria não encontrada."));

        boolean materiaPertenceUniversidade = materia.getGraduacoes().stream()
                .anyMatch(graduacao -> graduacao.getUniversidade().equals(universidade));
        if (!materiaPertenceUniversidade) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matéria não pertence à sua universidade.");
        }

        String diaSemanaSanitized = sanitizeDiaSemana(diaSemana);
        String turnoSanitized = sanitizeTurno(turno);

        return alunoRepository.findAlunosElegiveisParaTurma(universidade.getId(), campus, materiaId, diaSemanaSanitized, turnoSanitized);
    }

    @Transactional(readOnly = true)
    public List<Aluno> findEligibleStudentsForEdit(Long turmaId, String campus, Long materiaId, String diaSemana, String turno) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matéria não encontrada."));

        boolean materiaPertenceUniversidade = materia.getGraduacoes().stream()
                .anyMatch(graduacao -> graduacao.getUniversidade().equals(universidade));
        if (!materiaPertenceUniversidade) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matéria não pertence à sua universidade.");
        }

        String diaSemanaSanitized = sanitizeDiaSemana(diaSemana);
        String turnoSanitized = sanitizeTurno(turno);

        return alunoRepository.findAlunosElegiveisParaTurmaComEdicao(universidade.getId(), campus, materiaId, diaSemanaSanitized, turnoSanitized, turmaId);
    }

    public List<TurmaDTO> findAllByLoggedInUserUniversity() {
        Universidade uniFuncionario = getUniversidadeDoFuncionarioLogado();
        List<Turma> turmas = turmaRepository.findAllByProfessor_Universidade_Id(uniFuncionario.getId());
        return turmas.stream().map(TurmaDTO::new).collect(Collectors.toList());
    }

    public TurmaDTO findTurmaById(Long turmaId) {
        Turma turma = findByIdAndLoggedInUserUniversity(turmaId);
        return new TurmaDTO(turma);
    }

    private Turma findById(Long turmaId) {
        return turmaRepository.findById(turmaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada."));
    }

    public Turma buscarTurmaPorId(Long turmaId) {
        return findById(turmaId);
    }

    public Turma findByIdAndLoggedInUserUniversity(Long turmaId) {
        Universidade uniFuncionario = getUniversidadeDoFuncionarioLogado();
        return turmaRepository.findByIdAndProfessor_Universidade(turmaId, uniFuncionario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada ou não pertence à sua universidade."));
    }


    @Transactional
    public void delete(Long turmaId) {
        Turma turma = findByIdAndLoggedInUserUniversity(turmaId);
        turma.getAlunos().clear();
        turmaRepository.delete(turma);
    }

    public List<Turma> findAllByAlunoId(Long alunoId) {
        return turmaRepository.findAllByAlunoIdWithDetails(alunoId);
    }
} 