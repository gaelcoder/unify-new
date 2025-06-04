package com.projeto.unify.controllers;

import com.projeto.unify.models.Professor;
import com.projeto.unify.services.ProfessorService; // Your backend ProfessorService
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/graduacao-operacoes") // A more general path for graduation-related operations
@RequiredArgsConstructor
public class GraduacaoOperacoesController {

    private final ProfessorService professorService; // Your backend ProfessorService

    // Endpoint to list professors for a given university, intended for selection as coordinator
    // The backend ProfessorService should filter out those already coordinating other courses.
    // Security for this endpoint is handled in SecurityConfig.java
    @GetMapping("/professores/por-universidade/{universidadeId}")
    public ResponseEntity<List<Professor>> listarProfessoresDisponiveisParaCoordenacao(
            @PathVariable Long universidadeId) {
        // This method in your backend ProfessorService MUST exist and perform the necessary filtering
        List<Professor> professores = professorService.listarProfessoresPorUniversidadeDisponiveisParaCoordenacao(universidadeId);
        return ResponseEntity.ok(professores);
    }
} 