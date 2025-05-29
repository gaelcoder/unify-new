package com.projeto.unify.controllers;

import com.projeto.unify.dtos.UniversidadeStatsDTO;
import com.projeto.unify.services.UniversidadeService;
import com.projeto.unify.dtos.FuncionarioDTO;
import com.projeto.unify.models.Funcionario;
import com.projeto.unify.services.FuncionarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin-universidade") // Base path for this controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN_UNIVERSIDADE')") // Secure all endpoints for university admins
@CrossOrigin(origins = "*") // Consider restricting this in production
public class AdministradorUniversidadeController {

    private final UniversidadeService universidadeService;
    private final FuncionarioService funcionarioService;

    @GetMapping("/minha-universidade/stats")
    public ResponseEntity<UniversidadeStatsDTO> getMinhaUniversidadeStats() {
        UniversidadeStatsDTO stats = universidadeService.getStatsMinhaUniversidade();
        return ResponseEntity.ok(stats);
    }

    // Funcionario Endpoints for Admin Universidade
    @PostMapping("/funcionarios")
    public ResponseEntity<Funcionario> criarFuncionario(@Valid @RequestBody FuncionarioDTO funcionarioDTO) {
        Funcionario novoFuncionario = funcionarioService.criar(funcionarioDTO);
        return new ResponseEntity<>(novoFuncionario, HttpStatus.CREATED);
    }

    @GetMapping("/funcionarios")
    public ResponseEntity<List<Funcionario>> listarFuncionariosDaUniversidade() {
        List<Funcionario> funcionarios = funcionarioService.listarTodosPorUniversidadeDoUsuarioLogado();
        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/funcionarios/{id}")
    public ResponseEntity<Funcionario> buscarFuncionarioDaUniversidadePorId(@PathVariable Long id) {
        Funcionario funcionario = funcionarioService.buscarPorIdEUniversidadeDoUsuarioLogado(id);
        return ResponseEntity.ok(funcionario);
    }

    @PutMapping("/funcionarios/{id}")
    public ResponseEntity<Funcionario> atualizarFuncionarioDaUniversidade(@PathVariable Long id, @Valid @RequestBody FuncionarioDTO funcionarioDTO) {
        Funcionario funcionarioAtualizado = funcionarioService.atualizar(id, funcionarioDTO);
        return ResponseEntity.ok(funcionarioAtualizado);
    }

    @DeleteMapping("/funcionarios/{id}")
    public ResponseEntity<Void> deletarFuncionarioDaUniversidade(@PathVariable Long id) {
        funcionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Add other specific endpoints for ADMIN_UNIVERSIDADE related to their university management
    // (not directly CRUD on other entities like Funcionario which have their own controllers)

}
