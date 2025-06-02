package com.projeto.unify.controllers;

import com.projeto.unify.dtos.NotaDTO;
import com.projeto.unify.models.Nota;
import com.projeto.unify.services.NotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Adjust as needed
public class NotaController {

    private final NotaService notaService;

    // Helper to convert Nota Entity to DTO (if needed for response)
    private NotaDTO convertToDto(Nota nota) {
        return NotaDTO.builder()
                .id(nota.getId())
                .alunoId(nota.getAluno().getId())
                .avaliacaoId(nota.getAvaliacao().getId())
                .valorObtido(nota.getValorObtido())
                .observacoes(nota.getObservacoes())
                .build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
    public ResponseEntity<NotaDTO> lancarOuAtualizarNota(@Valid @RequestBody NotaDTO notaDTO) {
        Nota notaSalva = notaService.lancarOuAtualizarNota(notaDTO);
        return new ResponseEntity<>(convertToDto(notaSalva), HttpStatus.OK); // OK for create or update
    }

    @GetMapping("/avaliacao/{avaliacaoId}")
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
    public ResponseEntity<List<NotaDTO>> listarNotasDaAvaliacao(@PathVariable Long avaliacaoId) {
        List<Nota> notas = notaService.listarNotasDaAvaliacao(avaliacaoId);
        List<NotaDTO> dtos = notas.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/aluno/minhas-notas/turma/{turmaId}")
    @PreAuthorize("hasAuthority('ROLE_ALUNO')")
    public ResponseEntity<List<NotaDTO>> listarMinhasNotasNaTurma(@PathVariable Long turmaId) {
        List<Nota> notas = notaService.listarNotasDoAlunoLogadoNaTurma(turmaId);
        List<NotaDTO> dtos = notas.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/aluno/{alunoId}/turma/{turmaId}")
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR') or (hasAuthority('ROLE_ALUNO') and @customSecurityService.isAlunoLogadoEProprioDonoDosDados(#alunoId))")
    public ResponseEntity<List<NotaDTO>> listarNotasDoAlunoNaTurma(@PathVariable Long alunoId, @PathVariable Long turmaId) {
        List<Nota> notas = notaService.listarNotasDoAlunoNaTurma(alunoId, turmaId);
        List<NotaDTO> dtos = notas.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/aluno/minha-media/turma/{turmaId}")
    @PreAuthorize("hasAuthority('ROLE_ALUNO')")
    public ResponseEntity<Map<String, Double>> calcularMinhaMediaFinalNaTurma(@PathVariable Long turmaId) {
        Double media = notaService.calcularMediaFinalAlunoLogadoNaTurma(turmaId);
        if (media == null) {
            return ResponseEntity.ok(Map.of("media", 0.0));
        }
        return ResponseEntity.ok(Map.of("media", media));
    }

    @GetMapping("/aluno/{alunoId}/turma/{turmaId}/media")
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR') or (hasAuthority('ROLE_ALUNO') and @customSecurityService.isAlunoLogadoEProprioDonoDosDados(#alunoId))")
    public ResponseEntity<Map<String, Double>> calcularMediaFinalAlunoNaTurma(@PathVariable Long alunoId, @PathVariable Long turmaId) {
        Double media = notaService.calcularMediaFinalAlunoNaTurma(alunoId, turmaId);
        if (media == null) {
            // Isso pode acontecer se não houver notas ou avaliações, ou se a média for genuinamente 0 após cálculo
            // O serviço retorna 0.0 em caso de não haver avaliações. Se for null do repo, significa que não há notas para as avaliações existentes.
            return ResponseEntity.ok(Map.of("media", 0.0)); // ou HttpStatus.NOT_FOUND se preferir que média nula seja um erro
        }
        return ResponseEntity.ok(Map.of("media", media));
    }

    @GetMapping("/aluno/meu-status/turma/{turmaId}")
    @PreAuthorize("hasAuthority('ROLE_ALUNO')")
    public ResponseEntity<Map<String, String>> verificarMeuStatusAprovacao(@PathVariable Long turmaId) {
        String status = notaService.verificarStatusAprovacaoAlunoLogado(turmaId);
        return ResponseEntity.ok(Map.of("statusAprovacao", status));
    }

    @GetMapping("/aluno/{alunoId}/turma/{turmaId}/status")
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR') or (hasAuthority('ROLE_ALUNO') and @customSecurityService.isAlunoLogadoEProprioDonoDosDados(#alunoId))")
    public ResponseEntity<Map<String, String>> verificarStatusAprovacao(@PathVariable Long alunoId, @PathVariable Long turmaId) {
        String status = notaService.verificarStatusAprovacao(alunoId, turmaId);
        return ResponseEntity.ok(Map.of("statusAprovacao", status));
    }

    // TODO: Adicionar endpoints para buscar uma nota específica, deletar nota (se permitido)
} 