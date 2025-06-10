package com.projeto.unify.controllers;

import com.projeto.unify.dtos.SolicitacaoDTO;
import com.projeto.unify.models.Solicitacao;
import com.projeto.unify.services.SolicitacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @PostMapping
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<Solicitacao> create(@RequestBody @Valid SolicitacaoDTO solicitacaoDTO) {
        Solicitacao novaSolicitacao = solicitacaoService.create(solicitacaoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaSolicitacao);
    }
} 