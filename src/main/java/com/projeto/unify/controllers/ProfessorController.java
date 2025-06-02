package com.projeto.unify.controllers;

import com.projeto.unify.dtos.TurmaDTO;
import com.projeto.unify.models.Professor;
import com.projeto.unify.models.Turma;
import com.projeto.unify.services.ProfessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/professor")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
public class ProfessorController {

    private final ProfessorService professorService;

    private Professor getProfessorLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        return professorService.buscarPorUsuarioEmail(emailUsuarioLogado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Professor não encontrado ou não autenticado."));
    }

    @GetMapping("/minhas-turmas")
    public ResponseEntity<List<TurmaDTO>> getMinhasTurmas() {
        Professor professorLogado = getProfessorLogado();
        List<Turma> turmas = professorLogado.getTurmas().stream().collect(Collectors.toList());

        List<TurmaDTO> turmaDTOs = turmas.stream()
                .map(this::convertToTurmaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(turmaDTOs);
    }

    private TurmaDTO convertToTurmaDTO(Turma turma) {
        TurmaDTO dto = TurmaDTO.builder()
                .id(turma.getId())
                .nomeMateria(turma.getMateria() != null ? turma.getMateria().getTitulo() : null)
                .materiaId(turma.getMateria() != null ? turma.getMateria().getId() : null)
                .nomeProfessor(turma.getProfessor() != null ? turma.getProfessor().getNomeCompleto() : null)
                .professorId(turma.getProfessor() != null ? turma.getProfessor().getId() : null)
                .nomeGraduacao(turma.getGraduacao() != null ? turma.getGraduacao().getTitulo() : null)
                .graduacaoId(turma.getGraduacao() != null ? turma.getGraduacao().getId() : null)
                .codigoGraduacao(turma.getGraduacao() != null ? turma.getGraduacao().getCodigoCurso() : null)
                .turno(turma.getTurno())
                .limiteAlunos(turma.getLimiteAlunos())
                .numeroAlunosMatriculados(turma.getAlunos() != null ? turma.getAlunos().size() : 0)
                .build();
        // Calcular vagas disponíveis
        dto.setVagasDisponiveis(turma.getLimiteAlunos() - dto.getNumeroAlunosMatriculados());
        return dto;
    }
} 