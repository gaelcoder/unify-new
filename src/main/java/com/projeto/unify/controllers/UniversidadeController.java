package com.projeto.unify.controllers;

import com.projeto.unify.dtos.UniversidadeCampusDTO;
import com.projeto.unify.models.Universidade;
import com.projeto.unify.services.UniversidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/universidades")
public class UniversidadeController {

    @Autowired
    private UniversidadeService universidadeService;

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