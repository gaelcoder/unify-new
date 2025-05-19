package com.projeto.unify.controllers;

import com.projeto.unify.services.UniversidadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.projeto.unify.dtos.*;
import com.projeto.unify.services.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final UniversidadeService universidadeService;
    private final RepresentanteService representanteService;

    @PostMapping("/universidades")
    public ResponseEntity<?> criarUniversidade(@Valid @RequestBody UniversidadeDTO dto) {
        return ResponseEntity.ok(universidadeService.criar(dto));
    }

    @PostMapping("/representantes")
    public ResponseEntity<?> criarRepresentante(@Valid @RequestBody RepresentanteDTO dto) {
        return ResponseEntity.ok(representanteService.criar(dto));
    }

    @GetMapping("/universidades")
    public ResponseEntity<?> listarUniversidades() {
        return ResponseEntity.ok(universidadeService.listarTodos());
    }
}
