package com.projeto.unify.controllers;

import com.projeto.unify.dtos.GraduacaoDTO;
import com.projeto.unify.models.Graduacao;
import com.projeto.unify.services.GraduacaoService;
import com.projeto.unify.dtos.MateriaDTO;
import com.projeto.unify.models.Materia;
import com.projeto.unify.services.MateriaService;
import com.projeto.unify.dtos.TurmaDTO;
import com.projeto.unify.models.Turma;
import com.projeto.unify.services.TurmaService;
import com.projeto.unify.dtos.AlunoDTO;
import com.projeto.unify.models.Aluno;
import com.projeto.unify.services.AlunoService;
import com.projeto.unify.dtos.SecretariaDashboardStatsDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/funcionariosecretaria")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasAuthority('ROLE_FUNCIONARIO')")
public class FuncionarioSecretariaController {

    private static final Logger logger = LoggerFactory.getLogger(FuncionarioSecretariaController.class);
    private final GraduacaoService graduacaoService;
    private final MateriaService materiaService;
    private final TurmaService turmaService;
    private final AlunoService alunoService;

    // --- Dashboard Stats Endpoint ---
    @GetMapping("/dashboard/stats")
    public ResponseEntity<SecretariaDashboardStatsDTO> getDashboardStats() {
        // TODO: Implement actual logic to fetch stats from services/repositories
        // For now, returning dummy data
        SecretariaDashboardStatsDTO stats = new SecretariaDashboardStatsDTO(0L, 0L);
        return ResponseEntity.ok(stats);
    }

    // --- Graduacao CRUD Endpoints ---

    @PostMapping("/graduacoes")
    public ResponseEntity<Graduacao> criarGraduacao(@Valid @RequestBody GraduacaoDTO graduacaoDTO) {
        Graduacao novaGraduacao = graduacaoService.criar(graduacaoDTO);
        return new ResponseEntity<>(novaGraduacao, HttpStatus.CREATED);
    }

    @GetMapping("/graduacoes")
    public ResponseEntity<List<Graduacao>> listarGraduacoes() {
        List<Graduacao> graduacoes = graduacaoService.listarTodasPorUniversidadeDoUsuarioLogado();
        return ResponseEntity.ok(graduacoes);
    }

    @GetMapping("/graduacoes/{id}")
    public ResponseEntity<Graduacao> buscarGraduacaoPorId(@PathVariable Long id) {
        Graduacao graduacao = graduacaoService.buscarPorId(id);
        return ResponseEntity.ok(graduacao);
    }

    @PutMapping("/graduacoes/{id}")
    public ResponseEntity<Graduacao> atualizarGraduacao(@PathVariable Long id, @Valid @RequestBody GraduacaoDTO graduacaoDTO) {
        Graduacao graduacaoAtualizada = graduacaoService.atualizar(id, graduacaoDTO);
        return ResponseEntity.ok(graduacaoAtualizada);
    }

    @DeleteMapping("/graduacoes/{id}")
    public ResponseEntity<Void> deletarGraduacao(@PathVariable Long id) {
        graduacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // --- Materia CRUD Endpoints ---

    @PostMapping("/materias")
    public ResponseEntity<Materia> criarMateria(@Valid @RequestBody MateriaDTO materiaDTO) {
        Materia novaMateria = materiaService.criarMateria(materiaDTO);
        return new ResponseEntity<>(novaMateria, HttpStatus.CREATED);
    }

    @GetMapping("/materias")
    public ResponseEntity<List<Materia>> listarMaterias() {
        List<Materia> materias = materiaService.listarMateriasPorUniversidadeDoFuncionarioLogado();
        return ResponseEntity.ok(materias);
    }

    @GetMapping("/materias/{id}")
    public ResponseEntity<Materia> buscarMateriaPorId(@PathVariable Long id) {
        Materia materia = materiaService.buscarMateriaPorIdEUniversidade(id);
        return ResponseEntity.ok(materia);
    }

    @PutMapping("/materias/{id}")
    public ResponseEntity<Materia> atualizarMateria(@PathVariable Long id, @Valid @RequestBody MateriaDTO materiaDTO) {
        Materia materiaAtualizada = materiaService.atualizarMateria(id, materiaDTO);
        return ResponseEntity.ok(materiaAtualizada);
    }

    @DeleteMapping("/materias/{id}")
    public ResponseEntity<Void> deletarMateria(@PathVariable Long id) {
        materiaService.deletarMateria(id);
        return ResponseEntity.noContent().build();
    }

    // --- Turma CRUD Endpoints ---

    @PostMapping("/turmas")
    public ResponseEntity<Turma> criarTurma(@Valid @RequestBody TurmaDTO turmaDTO) {
        Turma novaTurma = turmaService.criar(turmaDTO);
        return new ResponseEntity<>(novaTurma, HttpStatus.CREATED);
    }

    @GetMapping("/turmas")
    public ResponseEntity<List<Turma>> listarTurmas() {
        List<Turma> turmas = turmaService.listarTurmasPorUniversidadeDoFuncionarioLogado();
        return ResponseEntity.ok(turmas);
    }

    @GetMapping("/turmas/{id}")
    public ResponseEntity<Turma> buscarTurmaPorId(@PathVariable Long id) {
        Turma turma = turmaService.buscarTurmaPorIdEUniversidadeDoFuncionarioLogado(id);
        return ResponseEntity.ok(turma);
    }

    @PutMapping("/turmas/{id}")
    public ResponseEntity<Turma> atualizarTurma(@PathVariable Long id, @Valid @RequestBody TurmaDTO turmaDTO) {
        Turma turmaAtualizada = turmaService.atualizar(id, turmaDTO);
        return ResponseEntity.ok(turmaAtualizada);
    }

    @DeleteMapping("/turmas/{id}")
    public ResponseEntity<Void> deletarTurma(@PathVariable Long id) {
        turmaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // --- Aluno CRUD Endpoints ---

    @PostMapping("/alunos")
    public ResponseEntity<Aluno> criarAluno(@Valid @RequestBody AlunoDTO alunoDTO) {
        Aluno novoAluno = alunoService.criar(alunoDTO);
        return new ResponseEntity<>(novoAluno, HttpStatus.CREATED);
    }

    @GetMapping("/alunos")
    public ResponseEntity<List<Aluno>> listarAlunos() {
        List<Aluno> alunos = alunoService.listarPorUniversidadeDoFuncionarioLogado();
        logger.info("Retornando lista de alunos. Quantidade: {}", alunos != null ? alunos.size() : "null");
        if (alunos != null && alunos.isEmpty()) {
            logger.info("A lista de alunos está vazia.");
        }
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/alunos/{id}")
    public ResponseEntity<Aluno> buscarAlunoPorId(@PathVariable Long id) {
        Aluno aluno = alunoService.buscarPorIdEUniversidadeDoFuncionarioLogado(id);
        return ResponseEntity.ok(aluno);
    }

    @PutMapping("/alunos/{id}")
    public ResponseEntity<Aluno> atualizarAluno(@PathVariable Long id, @Valid @RequestBody AlunoDTO alunoDTO) {
        Aluno alunoAtualizado = alunoService.atualizar(id, alunoDTO);
        return ResponseEntity.ok(alunoAtualizado);
    }

    @DeleteMapping("/alunos/{id}")
    public ResponseEntity<Void> deletarAluno(@PathVariable Long id) {
        alunoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
