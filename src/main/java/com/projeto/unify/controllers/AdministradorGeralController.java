package com.projeto.unify.controllers;

import com.projeto.unify.dtos.AdministradorGeralDTO;
import com.projeto.unify.dtos.RepresentanteDTO;
import com.projeto.unify.dtos.UniversidadeDTO;
import com.projeto.unify.models.AdministradorGeral;
import com.projeto.unify.models.Representante;
import com.projeto.unify.models.Universidade;
import com.projeto.unify.services.AdministradorGeralService;
import com.projeto.unify.services.RepresentanteService;
import com.projeto.unify.services.UniversidadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import com.projeto.unify.dtos.AdminGeralStatsDTO;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN_GERAL')")
public class AdministradorGeralController {

    private final RepresentanteService representanteService;
    private final UniversidadeService universidadeService;

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

    // Adicionar endpoint GET para buscar universidade por ID
    @GetMapping("/universidades/{id}")
    public ResponseEntity<Universidade> buscarUniversidade(@PathVariable Long id) {
        return ResponseEntity.ok(universidadeService.buscarPorId(id));
    }

    @GetMapping("/representantes/disponiveis")
    public ResponseEntity<List<Representante>> listarRepresentantesDisponiveis() {
        return ResponseEntity.ok(representanteService.listarDisponiveis());
    }

    // ENDPOINTS POST


    @PostMapping(value = "/universidades", consumes = { "multipart/form-data" })
    public ResponseEntity<?> criarUniversidade(@RequestPart("universidade") UniversidadeDTO dto,
                                               @RequestPart(value = "logo", required = false) MultipartFile logoFile) {
        try {
            Universidade universidade = universidadeService.criar(dto, logoFile);
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

    // ENDPOINTS PUT
    @PutMapping(value = "/universidades/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<Universidade> atualizarUniversidade(@PathVariable Long id,
                                                              @RequestPart("universidade") UniversidadeDTO dto,
                                                              @RequestPart(value = "logo", required = false) MultipartFile logoFile) {
        return ResponseEntity.ok(universidadeService.atualizar(id, dto, logoFile));
    }

    @PutMapping("/universidades/{universidadeId}/representante/{representanteId}")
    public ResponseEntity<Universidade> associarRepresentante(@PathVariable Long universidadeId, @PathVariable Long representanteId) {
        return ResponseEntity.ok(universidadeService.associarRepresentante(universidadeId, representanteId));
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

    // ENDPOINTS DELETE

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

    @DeleteMapping("/universidades/{id}/representante")
    public ResponseEntity<Universidade> desassociarRepresentante(@PathVariable Long id) {
        return ResponseEntity.ok(universidadeService.desassociarRepresentante(id));
    }

    // Stats endpoint for Admin Geral
    @GetMapping("/stats")
    public ResponseEntity<AdminGeralStatsDTO> getAdminGeralStats() {
        long totalUniversidades = universidadeService.countTotalUniversidades();
        AdminGeralStatsDTO stats = new AdminGeralStatsDTO(totalUniversidades);
        return ResponseEntity.ok(stats);
    }

}