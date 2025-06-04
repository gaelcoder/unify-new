package com.projeto.unify.controllers;

import com.projeto.unify.dtos.GraduacaoDTO;
import com.projeto.unify.models.Graduacao;
import com.projeto.unify.services.GraduacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/graduacoes")
@RequiredArgsConstructor
public class GraduacaoController {

    // private static final Logger logger = LoggerFactory.getLogger(GraduacaoController.class);
    private final GraduacaoService graduacaoService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO')")
    public ResponseEntity<Graduacao> criarGraduacao(@Valid @RequestBody GraduacaoDTO graduacaoDTO) {
        Graduacao novaGraduacao = graduacaoService.criar(graduacaoDTO);
        return new ResponseEntity<>(novaGraduacao, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO')")
    public ResponseEntity<List<Graduacao>> listarGraduacoes() {
        List<Graduacao> graduacoes = graduacaoService.listarTodasPorUniversidadeDoUsuarioLogado();
        // Log the data before sending
        System.out.println("Graduacoes to be sent to frontend: " + graduacoes);
        if (graduacoes != null && !graduacoes.isEmpty()) {
            for (Graduacao g : graduacoes) {
                System.out.println("Graduacao ID: " + g.getId() + ", Campi: " + g.getCampusDisponiveis());
                // logger.info("Graduacao ID: {}, Campi: {}", g.getId(), g.getCampusDisponiveis());
            }
        }
        return ResponseEntity.ok(graduacoes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO')")
    public ResponseEntity<Graduacao> buscarGraduacaoPorId(@PathVariable Long id) {
        Graduacao graduacao = graduacaoService.buscarPorId(id);
        return ResponseEntity.ok(graduacao);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO')")
    public ResponseEntity<Graduacao> atualizarGraduacao(@PathVariable Long id, @Valid @RequestBody GraduacaoDTO graduacaoDTO) {
        Graduacao graduacaoAtualizada = graduacaoService.atualizar(id, graduacaoDTO);
        return ResponseEntity.ok(graduacaoAtualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO')")
    public ResponseEntity<Void> deletarGraduacao(@PathVariable Long id) {
        graduacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
} 