package com.projeto.unify.controllers;

import com.projeto.unify.dtos.ProfessorDTO;
import com.projeto.unify.models.Professor;
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
import com.projeto.unify.services.ProfessorService;

import java.util.List;

@RestController
@RequestMapping("/api/rh")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_FUNCIONARIO_RH')")
public class FuncionarioRHController {

    private final UniversidadeService universidadeService;
    private final FuncionarioService funcionarioService;
    private final ProfessorService professorService;

    //
    @PostMapping("/professores/")
    public ResponseEntity<Professor> criarProfessor(@Valid @RequestBody ProfessorDTO professorDTO) {
        Professor novoProfessor = professorService.criar(professorDTO);
        return new ResponseEntity<>(novoProfessor, HttpStatus.CREATED);
    }

    @GetMapping("/professores")
    public ResponseEntity<List<Professor>> listarProfessoresDaUniversidade() {
        List<Professor> professores = professorService.listarTodosPorUniversidadeDoUsuarioLogado();
        return ResponseEntity.ok(professores);
    }

    @GetMapping("/professores/{id}")
    public ResponseEntity<Professor> buscarProfessorDaUniversidadePorId(@PathVariable Long id) {
        Professor professor = professorService.buscarPorIdEUniversidadeDoUsuarioLogado(id);
        return ResponseEntity.ok(professor);
    }

    @PutMapping("/professores/{id}")
    public ResponseEntity<Professor> atualizarProfessorDaUniversidade(@PathVariable Long id, @Valid @RequestBody ProfessorDTO professorDTO) {
        Professor professorAtualizado = professorService.atualizarProfessor(id, professorDTO);
        return ResponseEntity.ok(professorAtualizado);
    }

    @DeleteMapping("/professores/{id}")
    public ResponseEntity<Void> deletarProfessorDaUniversidade(@PathVariable Long id) {
        professorService.deletarProfessor(id);
        return ResponseEntity.noContent().build();
    }

    // Funcionario Endpoints for RH Universidade
    @PostMapping("/funcionarios/")
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

}
