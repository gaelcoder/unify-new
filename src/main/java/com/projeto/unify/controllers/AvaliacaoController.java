package com.projeto.unify.controllers;

import com.projeto.unify.dtos.AvaliacaoDTO;
import com.projeto.unify.models.Avaliacao;
import com.projeto.unify.services.AvaliacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/avaliacoes") // Base path, specific paths per method
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Adjust as needed for security
@PreAuthorize("hasAuthority('ROLE_PROFESSOR')") // Changed from hasAnyAuthority to hasAuthority PROFESSOR only
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    // Helper to convert Entity to DTO (if needed for response, though service returns Entity for now)
    private AvaliacaoDTO convertToDto(Avaliacao avaliacao) {
        return AvaliacaoDTO.builder()
                .id(avaliacao.getId())
                .turmaId(avaliacao.getTurma().getId())
                .nome(avaliacao.getNome())
                .dataPrevista(avaliacao.getDataPrevista())
                .valorMaximoPossivel(avaliacao.getValorMaximoPossivel())
                .build();
    }

    @PostMapping
    public ResponseEntity<AvaliacaoDTO> criarAvaliacao(@Valid @RequestBody AvaliacaoDTO avaliacaoDTO) {
        Avaliacao novaAvaliacao = avaliacaoService.criarAvaliacao(avaliacaoDTO);
        return new ResponseEntity<>(convertToDto(novaAvaliacao), HttpStatus.CREATED);
    }

    @GetMapping("/turma/{turmaId}")
    @PreAuthorize("hasAnyAuthority('ROLE_PROFESSOR', 'ROLE_ALUNO')") // Alunos podem ver as avaliações da turma
    public ResponseEntity<List<AvaliacaoDTO>> listarAvaliacoesDaTurma(@PathVariable Long turmaId) {
        // TODO: Adicionar validação se o aluno logado pertence à turmaId, se for ALUNO
        List<Avaliacao> avaliacoes = avaliacaoService.listarAvaliacoesDaTurma(turmaId);
        List<AvaliacaoDTO> dtos = avaliacoes.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{avaliacaoId}/turma/{turmaId}") // Explicitly linking avaliacao to turma in path
    @PreAuthorize("hasAnyAuthority('ROLE_PROFESSOR', 'ROLE_ALUNO')")
    public ResponseEntity<AvaliacaoDTO> buscarAvaliacaoPorId(@PathVariable Long turmaId, @PathVariable Long avaliacaoId) {
        // TODO: Adicionar validação se o aluno logado pertence à turmaId, se for ALUNO
        Avaliacao avaliacao = avaliacaoService.buscarAvaliacaoPorId(turmaId, avaliacaoId);
        return ResponseEntity.ok(convertToDto(avaliacao));
    }

    @PutMapping("/{avaliacaoId}")
    public ResponseEntity<AvaliacaoDTO> atualizarAvaliacao(@PathVariable Long avaliacaoId, @Valid @RequestBody AvaliacaoDTO avaliacaoDTO) {
        // O DTO deve conter o turmaId para validação no serviço, ou o serviço busca a avaliação e sua turma.
        // O serviço já valida se o turmaId no DTO corresponde ao da avaliação existente se dto.turmaId for usado.
        Avaliacao avaliacaoAtualizada = avaliacaoService.atualizarAvaliacao(avaliacaoId, avaliacaoDTO);
        return ResponseEntity.ok(convertToDto(avaliacaoAtualizada));
    }

    @DeleteMapping("/{avaliacaoId}/turma/{turmaId}") // Path para garantir que a turma correta está sendo referenciada
    public ResponseEntity<Void> deletarAvaliacao(@PathVariable Long turmaId, @PathVariable Long avaliacaoId) {
        avaliacaoService.deletarAvaliacao(turmaId, avaliacaoId);
        return ResponseEntity.noContent().build();
    }
} 