package com.projeto.unify.controllers;

import com.projeto.unify.dtos.solicitacoes.AlunoTrocaTurmaRequestDTO;
import com.projeto.unify.dtos.solicitacoes.TrocaTurmaSolicitacaoResponseDTO;
import com.projeto.unify.dtos.solicitacoes.AlunoTransferenciaGraduacaoRequestDTO;
import com.projeto.unify.dtos.solicitacoes.TransferenciaGraduacaoSolicitacaoResponseDTO;
import com.projeto.unify.services.TrocaTurmaSolicitacaoService;
import com.projeto.unify.services.TransferenciaGraduacaoSolicitacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aluno/solicitacoes")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ALUNO')")
@CrossOrigin(origins = "*") // Ajustar conforme necessário
public class AlunoSolicitacaoController {

    private final TrocaTurmaSolicitacaoService trocaTurmaSolicitacaoService;
    private final TransferenciaGraduacaoSolicitacaoService transferenciaGraduacaoSolicitacaoService;

    @PostMapping("/troca-turma")
    public ResponseEntity<TrocaTurmaSolicitacaoResponseDTO> solicitarTrocaTurma(
            @Valid @RequestBody AlunoTrocaTurmaRequestDTO dto) {
        TrocaTurmaSolicitacaoResponseDTO solicitacaoCriada = trocaTurmaSolicitacaoService.criarSolicitacao(dto);
        return new ResponseEntity<>(solicitacaoCriada, HttpStatus.CREATED);
    }

    @GetMapping("/troca-turma/minhas")
    public ResponseEntity<List<TrocaTurmaSolicitacaoResponseDTO>> listarMinhasSolicitacoesTrocaTurma() {
        List<TrocaTurmaSolicitacaoResponseDTO> solicitacoes = trocaTurmaSolicitacaoService.listarMinhasSolicitacoes();
        return ResponseEntity.ok(solicitacoes);
    }

    @PostMapping("/transferencia-graduacao")
    public ResponseEntity<TransferenciaGraduacaoSolicitacaoResponseDTO> solicitarTransferenciaGraduacao(
            @Valid @RequestBody AlunoTransferenciaGraduacaoRequestDTO dto) {
        TransferenciaGraduacaoSolicitacaoResponseDTO solicitacaoCriada = transferenciaGraduacaoSolicitacaoService.criarSolicitacao(dto);
        return new ResponseEntity<>(solicitacaoCriada, HttpStatus.CREATED);
    }

    @GetMapping("/transferencia-graduacao/minhas")
    public ResponseEntity<List<TransferenciaGraduacaoSolicitacaoResponseDTO>> listarMinhasSolicitacoesTransferenciaGraduacao() {
        List<TransferenciaGraduacaoSolicitacaoResponseDTO> solicitacoes = transferenciaGraduacaoSolicitacaoService.listarMinhasSolicitacoes();
        return ResponseEntity.ok(solicitacoes);
    }
    
    // Futuramente, um aluno poderia querer cancelar uma solicitação PENDENTE.
    // @PatchMapping("/troca-turma/{solicitacaoId}/cancelar")
    // public ResponseEntity<Void> cancelarMinhaSolicitacaoTrocaTurma(@PathVariable Long solicitacaoId) { ... }
} 