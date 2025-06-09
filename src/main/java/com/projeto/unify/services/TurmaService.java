package com.projeto.unify.services;

import com.projeto.unify.dtos.TurmaCreateDTO;
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

    @Transactional
    public Turma create(TurmaCreateDTO dto) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();

        Professor professor = professorRepository.findById(dto.getProfessorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado."));
        if (!Objects.equals(professor.getUniversidade().getId(), universidade.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Professor não pertence à sua universidade.");
        }

        boolean professorJaTemTurmaNoTurno = turmaRepository.existsByProfessorAndTurno(professor, dto.getTurno().toUpperCase());
        if (professorJaTemTurmaNoTurno) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Professor " + professor.getNome() + " já leciona uma turma no turno da " + dto.getTurno() + ".");
        }

        Materia materia = materiaRepository.findById(dto.getMateriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matéria não encontrada."));

        if (!universidade.getCampus().contains(dto.getCampus())) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O campus '" + dto.getCampus() + "' não pertence a esta universidade.");
        }

        Turma turma = new Turma();
        turma.setProfessor(professor);
        turma.setMateria(materia);
        turma.setTurno(dto.getTurno().toUpperCase());
        turma.setCampus(dto.getCampus());
        turma.setLimiteAlunos(dto.getLimiteAlunos());

        if (dto.getAlunoIds() != null && !dto.getAlunoIds().isEmpty()) {
            if (dto.getAlunoIds().size() > dto.getLimiteAlunos()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Número de alunos excede o limite da turma.");
            }
            List<Aluno> alunos = alunoRepository.findAllById(dto.getAlunoIds());
            for (Aluno aluno : alunos) {
                if (!aluno.getUniversidade().equals(universidade)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno " + aluno.getNome() + " (ID: " + aluno.getId() + ") não pertence à sua universidade.");
                }
            }
            turma.setAlunos(new ArrayList<>(alunos));
        }

        return turmaRepository.save(turma);
    }

    @Transactional(readOnly = true)
    public List<Aluno> findEligibleStudents(String campus, Long materiaId) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matéria não encontrada."));

        boolean materiaPertenceUniversidade = materia.getGraduacoes().stream()
                .anyMatch(graduacao -> graduacao.getUniversidade().equals(universidade));
        if (!materiaPertenceUniversidade) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matéria não pertence à sua universidade.");
        }

        return alunoRepository.findAlunosElegiveisParaTurma(universidade.getId(), campus, materiaId);
    }

    public List<TurmaDTO> findAllByLoggedInUserUniversity() {
        Universidade uniFuncionario = getUniversidadeDoFuncionarioLogado();
        List<Turma> turmas = turmaRepository.findByProfessor_Universidade(uniFuncionario);
        return turmas.stream().map(TurmaDTO::new).collect(Collectors.toList());
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
} 