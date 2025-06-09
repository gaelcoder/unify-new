package com.projeto.unify.controllers;

import com.projeto.unify.dtos.UniversidadeCampusDTO;
import com.projeto.unify.models.Universidade;
import com.projeto.unify.services.UniversidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/universidades")
@RequiredArgsConstructor
public class UniversidadeController {

    private final UniversidadeService universidadeService;

    @GetMapping("/minha-universidade")
    @PreAuthorize("hasAnyAuthority('ROLE_FUNCIONARIO', 'ROLE_ALUNO', 'ROLE_PROFESSOR', 'ROLE_ADMIN_UNIVERSIDADE')")
    public ResponseEntity<UniversidadeCampusDTO> getMinhaUniversidade() {
        Universidade universidade = universidadeService.findMyUniversity();
        return ResponseEntity.ok(new UniversidadeCampusDTO(universidade));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO')")
    public ResponseEntity<UniversidadeCampusDTO> buscarUniversidadePorId(@PathVariable Long id) {
        Universidade universidade = universidadeService.buscarPorId(id);
        if (universidade != null) {
            return ResponseEntity.ok(new UniversidadeCampusDTO(universidade));
        }
        return ResponseEntity.notFound().build();
    }
} 