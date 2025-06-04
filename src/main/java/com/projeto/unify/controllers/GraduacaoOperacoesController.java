package com.projeto.unify.controllers;

import com.projeto.unify.models.Professor;
import com.projeto.unify.services.ProfessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/graduacao-operacoes")
@RequiredArgsConstructor
public class GraduacaoOperacoesController {

    private final ProfessorService professorService;

    @GetMapping("/professores/por-universidade/{universidadeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_FUNCIONARIO', 'ROLE_FUNCIONARIO_RH')")
    public ResponseEntity<List<Professor>> listarProfessoresPorUniversidadeParaCoordenacao(@PathVariable Long universidadeId) {
        List<Professor> professores = professorService.listarProfessoresPorUniversidadeDisponiveisParaCoordenacao(universidadeId);
        return ResponseEntity.ok(professores);
    }
} 