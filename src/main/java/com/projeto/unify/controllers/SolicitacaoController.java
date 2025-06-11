package com.projeto.unify.controllers;

import com.projeto.unify.dtos.SolicitacaoDTO;
import com.projeto.unify.dtos.SolicitacaoSecretariaDTO;
import com.projeto.unify.dtos.SolicitacaoStatusUpdateDTO;
import com.projeto.unify.models.Solicitacao;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.models.Funcionario;
import com.projeto.unify.repositories.FuncionarioRepository;
import com.projeto.unify.services.SolicitacaoService;
import com.projeto.unify.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @GetMapping("/aluno/{alunoId}")
    @PreAuthorize("hasAuthority('ROLE_ALUNO') or hasAuthority('ROLE_FUNCIONARIO')")
    public ResponseEntity<List<Solicitacao>> getByAlunoId(@PathVariable Long alunoId) {
        List<Solicitacao> solicitacoes = solicitacaoService.findByAlunoId(alunoId);
        return ResponseEntity.ok(solicitacoes);
    }

    @GetMapping("/secretaria")
    @PreAuthorize("hasRole('FUNCIONARIO')")
    public ResponseEntity<List<SolicitacaoSecretariaDTO>> getForSecretaria(
            @RequestParam(required = false) String campus,
            @RequestParam(defaultValue = "ABERTA") Solicitacao.StatusSolicitacao status) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.findByEmail(userEmail);

        Funcionario funcionario = funcionarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado para o usuário: " + userEmail));

        if (funcionario.getUniversidade() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Long idUniversidade = funcionario.getUniversidade().getId();
        List<SolicitacaoSecretariaDTO> solicitacoes = solicitacaoService.findBySecretaria(idUniversidade, Optional.ofNullable(campus), status);
        return ResponseEntity.ok(solicitacoes);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('FUNCIONARIO')")
    public ResponseEntity<Solicitacao> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid SolicitacaoStatusUpdateDTO statusUpdateDTO) {
        Solicitacao solicitacao = solicitacaoService.updateStatus(id, statusUpdateDTO.status());
        return ResponseEntity.ok(solicitacao);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO')")
    public ResponseEntity<Solicitacao> create(@RequestBody @Valid SolicitacaoDTO solicitacaoDTO) {
        Solicitacao novaSolicitacao = solicitacaoService.create(solicitacaoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaSolicitacao);
    }
} 