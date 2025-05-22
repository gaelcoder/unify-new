package com.projeto.unify.controllers;

import com.projeto.unify.dtos.AdministradorGeralDTO;
import com.projeto.unify.models.AdministradorGeral;
import com.projeto.unify.services.AdministradorGeralService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/administradores")
@RequiredArgsConstructor
public class AdministradorGeralController {

    private final AdministradorGeralService administradorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_GERAL')")
    public ResponseEntity<AdministradorGeral> criar(@Valid @RequestBody AdministradorGeralDTO dto) {
        AdministradorGeral admin = administradorService.criarAdministrador(dto);
        return new ResponseEntity<>(admin, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN_GERAL')")
    public ResponseEntity<List<AdministradorGeral>> listarTodos() {
        List<AdministradorGeral> admins = administradorService.listarTodos();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_GERAL')")
    public ResponseEntity<AdministradorGeral> buscarPorId(@PathVariable Long id) {
        AdministradorGeral admin = administradorService.buscarPorId(id);
        return ResponseEntity.ok(admin);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_GERAL')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        administradorService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}