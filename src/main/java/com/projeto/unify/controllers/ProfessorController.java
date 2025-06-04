package com.projeto.unify.controllers;

import com.projeto.unify.dtos.ProfessorDTO;
import com.projeto.unify.models.Professor;
import com.projeto.unify.services.ProfessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/professores") // More general base path
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    // RH Specific Operations
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO_RH')")
    public ResponseEntity<Professor> criarProfessor(@Valid @RequestBody ProfessorDTO professorDTO) {
        Professor novoProfessor = professorService.criar(professorDTO);
        return new ResponseEntity<>(novoProfessor, HttpStatus.CREATED);
    }

    @GetMapping("/rh-lista") // Specific path for RH listing
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO_RH')")
    public ResponseEntity<List<Professor>> listarProfessoresParaRH() {
        List<Professor> professores = professorService.listarTodosPorUniversidadeDoUsuarioLogado();
        return ResponseEntity.ok(professores);
    }

    @GetMapping("/{id}/rh-details") // Specific path for RH to get full details
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO_RH')")
    public ResponseEntity<Professor> buscarProfessorPorIdParaRH(@PathVariable Long id) {
        Professor professor = professorService.buscarProfessorPorId(id); // Assumes this method exists
        return ResponseEntity.ok(professor);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO_RH')")
    public ResponseEntity<Professor> atualizarProfessor(@PathVariable Long id, @Valid @RequestBody ProfessorDTO professorDTO) {
        Professor professorAtualizado = professorService.atualizarProfessor(id, professorDTO);
        return ResponseEntity.ok(professorAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO_RH')")
    public ResponseEntity<Void> deletarProfessor(@PathVariable Long id) {
        professorService.deletarProfessor(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint for Graduation Form - Accessible by FUNCIONARIO and FUNCIONARIO_RH
    @GetMapping("/por-universidade/{universidadeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_FUNCIONARIO', 'ROLE_FUNCIONARIO_RH')")
    public ResponseEntity<List<Professor>> listarProfessoresDisponiveisParaCoordenacao(
            @PathVariable Long universidadeId) {
        List<Professor> professores = professorService.listarProfessoresPorUniversidadeDisponiveisParaCoordenacao(universidadeId);
        return ResponseEntity.ok(professores);
    }
}