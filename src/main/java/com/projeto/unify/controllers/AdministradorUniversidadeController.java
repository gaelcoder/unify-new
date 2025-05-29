package com.projeto.unify.controllers;

import com.projeto.unify.dtos.FuncionarioDTO;
import com.projeto.unify.models.Funcionario;
import com.projeto.unify.services.FuncionarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin-universidade") // Base path for this controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN_UNIVERSIDADE')") // Secure all endpoints for university admins
public class AdministradorUniversidadeController {

    private final FuncionarioService funcionarioService;

    @PostMapping("/funcionarios")
    public ResponseEntity<?> criarFuncionario(@Valid @RequestBody FuncionarioDTO funcionarioDTO) {
        try {
            Funcionario novoFuncionario = funcionarioService.criar(funcionarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoFuncionario);
        } catch (Exception e) {
            // Consider more specific exception handling if needed
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Add other endpoints for ADMIN_UNIVERSIDADE as needed, e.g.:
    // - Listar funcionarios da sua universidade
    // - Atualizar dados de um funcionario
    // - Desativar um funcionario

}
