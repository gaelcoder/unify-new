package com.projeto.unify.controllers;

import com.projeto.unify.dtos.GraduacaoDTO;
import com.projeto.unify.models.Graduacao;
import com.projeto.unify.services.GraduacaoService;
import com.projeto.unify.dtos.MateriaDTO;
import com.projeto.unify.models.Materia;
import com.projeto.unify.services.MateriaService;
import com.projeto.unify.dtos.AlunoDTO;
import com.projeto.unify.models.Aluno;
import com.projeto.unify.models.Professor;
import com.projeto.unify.services.AlunoService;
import com.projeto.unify.dtos.SecretariaDashboardStatsDTO;
import com.projeto.unify.dtos.TurmaCreateDTO;
import com.projeto.unify.dtos.TurmaDTO;
import com.projeto.unify.dtos.TurmaUpdateDTO;
import com.projeto.unify.models.Turma;
import com.projeto.unify.services.ProfessorService;
import com.projeto.unify.services.TurmaService;
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
    private final AlunoService alunoService;
    private final GraduacaoService graduacaoService;
    private final MateriaService materiaService;
    private final TurmaService turmaService;
    private final ProfessorService professorService;

    // --- Dashboard Stats Endpoint ---
    @GetMapping("/dashboard/stats")
    public ResponseEntity<SecretariaDashboardStatsDTO> getDashboardStats() {
        // TODO: Implement actual logic to fetch stats from services/repositories
        // For now, returning dummy data
        SecretariaDashboardStatsDTO stats = new SecretariaDashboardStatsDTO(0L, 0L);
        return ResponseEntity.ok(stats);
    }

    // --- Professor Endpoints ---
    @GetMapping("/professores")
    public ResponseEntity<List<Professor>> listarProfessores() {
        List<Professor> professores = professorService.listarTodosPorUniversidadeDoUsuarioLogado();
        return ResponseEntity.ok(professores);
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

    @GetMapping("/materias/{materiaId}/campuses")
    public ResponseEntity<List<String>> getCampusesForMateria(@PathVariable Long materiaId) {
        return ResponseEntity.ok(graduacaoService.findCampusesByMateriaId(materiaId));
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

    // --- Turma Endpoints ---

    @PostMapping("/turmas")
    public ResponseEntity<TurmaDTO> createTurma(@Valid @RequestBody TurmaCreateDTO turmaCreateDTO) {
        Turma novaTurma = turmaService.create(turmaCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TurmaDTO(novaTurma));
    }

    @PutMapping("/turmas/{id}")
    public ResponseEntity<TurmaDTO> updateTurma(@PathVariable Long id, @Valid @RequestBody TurmaUpdateDTO turmaUpdateDTO) {
        TurmaDTO turmaAtualizada = turmaService.update(id, turmaUpdateDTO);
        return ResponseEntity.ok(turmaAtualizada);
    }

    @GetMapping("/turmas/alunos-elegiveis")
    public ResponseEntity<List<Aluno>> getAlunosElegiveis(
            @RequestParam String campus,
            @RequestParam Long materiaId) {
        List<Aluno> alunos = turmaService.findEligibleStudents(campus, materiaId);
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/turmas")
    public ResponseEntity<List<TurmaDTO>> findAllTurmas() {
        return ResponseEntity.ok(turmaService.findAllByLoggedInUserUniversity());
    }

    @GetMapping("/turmas/{id}")
    public ResponseEntity<TurmaDTO> findTurmaById(@PathVariable Long id) {
        return ResponseEntity.ok(turmaService.findTurmaById(id));
    }

    @DeleteMapping("/turmas/{id}")
    public ResponseEntity<Void> deleteTurma(@PathVariable Long id) {
        turmaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
