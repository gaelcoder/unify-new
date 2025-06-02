package com.projeto.unify.controllers;

import com.projeto.unify.dtos.AlunoDTO;
import com.projeto.unify.dtos.MateriaDTO; // Será criado
import com.projeto.unify.dtos.GraduacaoDTO; // Descomentado
import com.projeto.unify.dtos.TurmaDTO; // Descomentado
import com.projeto.unify.models.Aluno;
import com.projeto.unify.models.Materia;
import com.projeto.unify.models.Graduacao;
import com.projeto.unify.models.Turma;
import com.projeto.unify.services.AlunoService;
import com.projeto.unify.services.MateriaService; // Será criado
import com.projeto.unify.services.GraduacaoService; // Descomentado e Injetado
import com.projeto.unify.services.TurmaService; // Descomentado e Injetado
import com.projeto.unify.services.TrocaTurmaSolicitacaoService;
import com.projeto.unify.dtos.solicitacoes.ProcessarTrocaTurmaDTO;
import com.projeto.unify.dtos.solicitacoes.TrocaTurmaSolicitacaoResponseDTO;
import com.projeto.unify.dtos.solicitacoes.ProcessarTransferenciaGraduacaoDTO;
import com.projeto.unify.dtos.solicitacoes.TransferenciaGraduacaoSolicitacaoResponseDTO;
import com.projeto.unify.services.TransferenciaGraduacaoSolicitacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.projeto.unify.dtos.SecretariaDashboardStatsDTO;

import java.util.List;
import java.util.stream.Collectors; // Para converter lista de Aluno para AlunoDTO se necessário
// Outras importações podem ser necessárias à medida que desenvolvemos os DTOs e Services

@RestController
@RequestMapping("/api/secretaria")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_FUNCIONARIO')") // Protege todos os endpoints para ROLE_FUNCIONARIO
@CrossOrigin(origins = "*") // Ajustar conforme necessário
public class SecretariaController {

    private final AlunoService alunoService;
    private final MateriaService materiaService; // Descomentado e Injetado
    private final GraduacaoService graduacaoService; // Descomentado e Injetado
    private final TurmaService turmaService; // Descomentado e Injetado
    private final TrocaTurmaSolicitacaoService trocaTurmaSolicitacaoService;
    private final TransferenciaGraduacaoSolicitacaoService transferenciaGraduacaoSolicitacaoService;

    // --- Dashboard Stats ---
    @GetMapping("/dashboard/stats")
    public ResponseEntity<SecretariaDashboardStatsDTO> getDashboardStats() {
        List<TrocaTurmaSolicitacaoResponseDTO> pendingTrocas = trocaTurmaSolicitacaoService.listarSolicitacoesPendentesSecretaria();
        List<TransferenciaGraduacaoSolicitacaoResponseDTO> pendingTransferencias = transferenciaGraduacaoSolicitacaoService.listarSolicitacoesPendentesSecretaria();

        SecretariaDashboardStatsDTO stats = SecretariaDashboardStatsDTO.builder()
                .pendingTrocaTurmaSolicitacoes(pendingTrocas.size())
                .pendingTransferenciaGraduacaoSolicitacoes(pendingTransferencias.size())
                .build();
        return ResponseEntity.ok(stats);
    }

    // --- Endpoints para Alunos ---
    @PostMapping("/alunos")
    public ResponseEntity<Aluno> criarAluno(@Valid @RequestBody AlunoDTO alunoDTO) {
        Aluno novoAluno = alunoService.criar(alunoDTO);
        // Idealmente, retornaria um AlunoResponseDTO para não expor a entidade diretamente
        return new ResponseEntity<>(novoAluno, HttpStatus.CREATED);
    }

    @GetMapping("/alunos")
    public ResponseEntity<List<Aluno>> listarAlunos() {
        List<Aluno> alunos = alunoService.listarAlunosDaUniversidadeLogada();
        // Considerar paginação e DTOs para a resposta
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/alunos/{id}")
    public ResponseEntity<Aluno> buscarAlunoPorId(@PathVariable Long id) {
        Aluno aluno = alunoService.buscarAlunoPorIdDaUniversidadeLogada(id);
        return ResponseEntity.ok(aluno);
    }

    @PutMapping("/alunos/{id}")
    public ResponseEntity<Aluno> atualizarAluno(@PathVariable Long id, @Valid @RequestBody AlunoDTO alunoDTO) {
        Aluno alunoAtualizado = alunoService.atualizarAluno(id, alunoDTO);
        return ResponseEntity.ok(alunoAtualizado);
    }

    @DeleteMapping("/alunos/{id}")
    public ResponseEntity<Void> deletarAluno(@PathVariable Long id) {
        alunoService.deletarAluno(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints para Matérias ---
    @PostMapping("/materias")
    public ResponseEntity<Materia> criarMateria(@Valid @RequestBody MateriaDTO materiaDTO) {
        Materia novaMateria = materiaService.criar(materiaDTO);
        return new ResponseEntity<>(novaMateria, HttpStatus.CREATED);
    }

    @GetMapping("/materias")
    public ResponseEntity<List<Materia>> listarMaterias() {
        List<Materia> materias = materiaService.listarMateriasDaUniversidadeLogada();
        return ResponseEntity.ok(materias);
    }

    @GetMapping("/materias/{id}")
    public ResponseEntity<Materia> buscarMateriaPorId(@PathVariable Long id) {
        Materia materia = materiaService.buscarMateriaPorIdDaUniversidadeLogada(id);
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

    // --- Endpoints para Graduações ---
    @PostMapping("/graduacoes")
    public ResponseEntity<Graduacao> criarGraduacao(@Valid @RequestBody GraduacaoDTO graduacaoDTO) {
        Graduacao novaGraduacao = graduacaoService.criar(graduacaoDTO);
        return new ResponseEntity<>(novaGraduacao, HttpStatus.CREATED);
    }

    @GetMapping("/graduacoes")
    public ResponseEntity<List<Graduacao>> listarGraduacoes() {
        List<Graduacao> graduacoes = graduacaoService.listarGraduacoesDaUniversidadeLogada();
        return ResponseEntity.ok(graduacoes);
    }

    @GetMapping("/graduacoes/{id}")
    public ResponseEntity<Graduacao> buscarGraduacaoPorId(@PathVariable Long id) {
        Graduacao graduacao = graduacaoService.buscarGraduacaoPorIdDaUniversidadeLogada(id);
        return ResponseEntity.ok(graduacao);
    }

    @PutMapping("/graduacoes/{id}")
    public ResponseEntity<Graduacao> atualizarGraduacao(@PathVariable Long id, @Valid @RequestBody GraduacaoDTO graduacaoDTO) {
        Graduacao graduacaoAtualizada = graduacaoService.atualizarGraduacao(id, graduacaoDTO);
        return ResponseEntity.ok(graduacaoAtualizada);
    }

    @DeleteMapping("/graduacoes/{id}")
    public ResponseEntity<Void> deletarGraduacao(@PathVariable Long id) {
        graduacaoService.deletarGraduacao(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints para Turmas ---
    @PostMapping("/turmas")
    public ResponseEntity<Turma> criarTurma(@Valid @RequestBody TurmaDTO turmaDTO) {
        Turma novaTurma = turmaService.criar(turmaDTO);
        return new ResponseEntity<>(novaTurma, HttpStatus.CREATED);
    }

    @GetMapping("/turmas")
    public ResponseEntity<List<Turma>> listarTurmas() {
        List<Turma> turmas = turmaService.listarTurmasDaUniversidadeLogada();
        return ResponseEntity.ok(turmas);
    }

    @GetMapping("/turmas/{id}")
    public ResponseEntity<Turma> buscarTurmaPorId(@PathVariable Long id) {
        Turma turma = turmaService.buscarTurmaPorIdDaUniversidadeLogada(id);
        return ResponseEntity.ok(turma);
    }

    @PutMapping("/turmas/{id}")
    public ResponseEntity<Turma> atualizarTurma(@PathVariable Long id, @Valid @RequestBody TurmaDTO turmaDTO) {
        Turma turmaAtualizada = turmaService.atualizarTurma(id, turmaDTO);
        return ResponseEntity.ok(turmaAtualizada);
    }

    @DeleteMapping("/turmas/{id}")
    public ResponseEntity<Void> deletarTurma(@PathVariable Long id) {
        turmaService.deletarTurma(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints para Matricular/Remover Aluno de Turma ---
    @PostMapping("/turmas/{turmaId}/alunos/{alunoId}")
    public ResponseEntity<Turma> matricularAlunoNaTurma(@PathVariable Long turmaId, @PathVariable Long alunoId) {
        Turma turmaAtualizada = turmaService.matricularAluno(turmaId, alunoId);
        return ResponseEntity.ok(turmaAtualizada); // Retorna a turma com a lista de alunos atualizada
    }

    @DeleteMapping("/turmas/{turmaId}/alunos/{alunoId}")
    public ResponseEntity<Turma> removerAlunoDaTurma(@PathVariable Long turmaId, @PathVariable Long alunoId) {
        Turma turmaAtualizada = turmaService.removerAlunoDaTurma(turmaId, alunoId);
        return ResponseEntity.ok(turmaAtualizada); // Retorna a turma com a lista de alunos atualizada
    }

    // --- Endpoints para Solicitações de Troca de Turma (Visão Secretaria) ---
    @GetMapping("/solicitacoes/troca-turma/pendentes")
    public ResponseEntity<List<TrocaTurmaSolicitacaoResponseDTO>> listarSolicitacoesTrocaTurmaPendentes() {
        List<TrocaTurmaSolicitacaoResponseDTO> solicitacoes = trocaTurmaSolicitacaoService.listarSolicitacoesPendentesSecretaria();
        return ResponseEntity.ok(solicitacoes);
    }

    @GetMapping("/solicitacoes/troca-turma/{solicitacaoId}")
    public ResponseEntity<TrocaTurmaSolicitacaoResponseDTO> buscarSolicitacaoTrocaTurmaPorId(@PathVariable Long solicitacaoId) {
        TrocaTurmaSolicitacaoResponseDTO solicitacao = trocaTurmaSolicitacaoService.buscarSolicitacaoPorIdSecretaria(solicitacaoId);
        return ResponseEntity.ok(solicitacao);
    }

    @PostMapping("/solicitacoes/troca-turma/{solicitacaoId}/processar")
    public ResponseEntity<TrocaTurmaSolicitacaoResponseDTO> processarSolicitacaoTrocaTurma(
            @PathVariable Long solicitacaoId,
            @Valid @RequestBody ProcessarTrocaTurmaDTO dto) {
        TrocaTurmaSolicitacaoResponseDTO solicitacaoProcessada = trocaTurmaSolicitacaoService.processarSolicitacao(solicitacaoId, dto);
        return ResponseEntity.ok(solicitacaoProcessada);
    }

    // --- Endpoints para Solicitações de Transferência de Graduação (Visão Secretaria) ---
    @GetMapping("/solicitacoes/transferencia-graduacao/pendentes")
    public ResponseEntity<List<TransferenciaGraduacaoSolicitacaoResponseDTO>> listarSolicitacoesTransferenciaGraduacaoPendentes() {
        List<TransferenciaGraduacaoSolicitacaoResponseDTO> solicitacoes = transferenciaGraduacaoSolicitacaoService.listarSolicitacoesPendentesSecretaria();
        return ResponseEntity.ok(solicitacoes);
    }

    @GetMapping("/solicitacoes/transferencia-graduacao/{solicitacaoId}")
    public ResponseEntity<TransferenciaGraduacaoSolicitacaoResponseDTO> buscarSolicitacaoTransferenciaGraduacaoPorId(@PathVariable Long solicitacaoId) {
        TransferenciaGraduacaoSolicitacaoResponseDTO solicitacao = transferenciaGraduacaoSolicitacaoService.buscarSolicitacaoPorIdSecretaria(solicitacaoId);
        return ResponseEntity.ok(solicitacao);
    }

    @PostMapping("/solicitacoes/transferencia-graduacao/{solicitacaoId}/processar")
    public ResponseEntity<TransferenciaGraduacaoSolicitacaoResponseDTO> processarSolicitacaoTransferenciaGraduacao(
            @PathVariable Long solicitacaoId,
            @Valid @RequestBody ProcessarTransferenciaGraduacaoDTO dto) {
        TransferenciaGraduacaoSolicitacaoResponseDTO solicitacaoProcessada = transferenciaGraduacaoSolicitacaoService.processarSolicitacao(solicitacaoId, dto);
        return ResponseEntity.ok(solicitacaoProcessada);
    }

} 