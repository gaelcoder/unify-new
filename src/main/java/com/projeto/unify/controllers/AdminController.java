package com.projeto.unify.controllers;

import com.projeto.unify.dtos.RepresentanteDTO;
import com.projeto.unify.dtos.UniversidadeDTO;
import com.projeto.unify.models.Representante;
import com.projeto.unify.models.Universidade;
import com.projeto.unify.services.RepresentanteService;
import com.projeto.unify.services.UniversidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Adicione esta anotação para permitir solicitações CORS
public class AdminController {

    private final UniversidadeService universidadeService;
    private final RepresentanteService representanteService;

    // Adicionar endpoint GET para buscar universidade por ID
    @GetMapping("/universidades/{id}")
    public ResponseEntity<Universidade> buscarUniversidade(@PathVariable Long id) {
        return ResponseEntity.ok(universidadeService.buscarPorId(id));
    }

    @PostMapping("/universidades")
    public ResponseEntity<?> criarUniversidade(@RequestBody UniversidadeDTO dto) {
        try {
            Universidade universidade = universidadeService.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(universidade);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/representantes")
    public ResponseEntity<?> criarRepresentante(@RequestBody RepresentanteDTO dto) {
        try {
            Representante representante = representanteService.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(representante);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/representantes/{id}")
    public ResponseEntity<Representante> buscarRepresentantePorId(@PathVariable Long id) {
        return ResponseEntity.ok(representanteService.buscarPorId(id));
    }


    @GetMapping("/representantes")
    public ResponseEntity<List<Representante>> listarRepresentantes() {
        return ResponseEntity.ok(representanteService.listarTodos());
    }

    @GetMapping("/universidades")
    public ResponseEntity<?> listarUniversidades() {
        return ResponseEntity.ok(universidadeService.listarTodos());
    }

    @PutMapping("/universidades/{id}")
    public ResponseEntity<Universidade> atualizarUniversidade(@PathVariable Long id, @RequestBody UniversidadeDTO dto) {
        return ResponseEntity.ok(universidadeService.atualizar(id, dto));
    }

    @PutMapping("/universidades/{universidadeId}/representante/{representanteId}")
    public ResponseEntity<Universidade> associarRepresentante(@PathVariable Long universidadeId, @PathVariable Long representanteId) {
        return ResponseEntity.ok(universidadeService.associarRepresentante(universidadeId, representanteId));
    }

    @DeleteMapping("/universidades/{id}")
    public ResponseEntity<Void> excluirUniversidade(@PathVariable Long id) {
        universidadeService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/representantes/{id}")
    public ResponseEntity<Void> excluirRepresentante(@PathVariable Long id) {
        representanteService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/representantes/{id}")
    public ResponseEntity<?> atualizarRepresentante(@PathVariable Long id, @RequestBody RepresentanteDTO dto) {
        try {
            Representante representante = representanteService.atualizar(id, dto);
            return ResponseEntity.ok(representante);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/universidades/{id}/representante")
    public ResponseEntity<Universidade> desassociarRepresentante(@PathVariable Long id) {
        return ResponseEntity.ok(universidadeService.desassociarRepresentante(id));
    }

    @GetMapping("/representantes/disponiveis")
    public ResponseEntity<List<Representante>> listarRepresentantesDisponiveis() {
        return ResponseEntity.ok(representanteService.listarDisponiveis());
    }
}