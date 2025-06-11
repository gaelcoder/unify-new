package com.projeto.unify.controllers;

import com.projeto.unify.dtos.ProfessorDTO;
import com.projeto.unify.dtos.TurmaProfessorDTO;
import com.projeto.unify.models.Professor;
import com.projeto.unify.models.Turma;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.services.PdfService;
import com.projeto.unify.services.ProfessorService;
import com.projeto.unify.services.TurmaService;
import com.projeto.unify.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/professores") // More general base path
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;
    private final UsuarioService usuarioService;
    private final TurmaService turmaService;
    private final PdfService pdfService;

    // RH Specific Operations
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO_RH')")
    public ResponseEntity<Professor> criarProfessor(@Valid @RequestBody ProfessorDTO professorDTO) {
        Professor novoProfessor = professorService.criar(professorDTO);
        return new ResponseEntity<>(novoProfessor, HttpStatus.CREATED);
    }

    @GetMapping("/rh-lista") // Specific path for RH listing
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO_RH')")
    public ResponseEntity<List<Professor>> listarProfessoresParaRH() {
        List<Professor> professores = professorService.listarTodosPorUniversidadeDoUsuarioLogado();
        return ResponseEntity.ok(professores);
    }

    @GetMapping("/{id}/rh-details") // Specific path for RH to get full details
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO_RH')")
    public ResponseEntity<Professor> buscarProfessorPorIdParaRH(@PathVariable Long id) {
        Professor professor = professorService.buscarProfessorPorId(id); // Assumes this method exists
        return ResponseEntity.ok(professor);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO_RH')")
    public ResponseEntity<Professor> atualizarProfessor(@PathVariable Long id, @Valid @RequestBody ProfessorDTO professorDTO) {
        Professor professorAtualizado = professorService.atualizarProfessor(id, professorDTO);
        return ResponseEntity.ok(professorAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_FUNCIONARIO_RH')")
    public ResponseEntity<Void> deletarProfessor(@PathVariable Long id) {
        professorService.deletarProfessor(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint for Graduation Form - Accessible by FUNCIONARIO and FUNCIONARIO_RH
    @GetMapping("/por-universidade/{universidadeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_FUNCIONARIO', 'ROLE_FUNCIONARIO_RH')")
    public ResponseEntity<List<Professor>> listarProfessoresDisponiveisParaCoordenacao(
            @PathVariable Long universidadeId) {
        List<Professor> professores = professorService.listarProfessoresPorUniversidadeDisponiveisParaCoordenacao(universidadeId);
        return ResponseEntity.ok(professores);
    }

    @GetMapping("/turmas")
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
    public ResponseEntity<List<TurmaProfessorDTO>> getTurmasDoProfessor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(userEmail);
        Professor professor = professorService.buscarPorUsuario(usuario);
        List<TurmaProfessorDTO> turmas = professorService.buscarTurmasDoProfessor(professor.getId());
        return ResponseEntity.ok(turmas);
    }

    @GetMapping("/turmas/{turmaId}/relatorio")
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
    public ResponseEntity<InputStreamResource> gerarRelatorioTurma(@PathVariable Long turmaId) {
        try {
            Turma turma = turmaService.buscarTurmaPorId(turmaId);
            ByteArrayInputStream bis = pdfService.gerarRelatorioTurmaPdf(turma);

            HttpHeaders headers = new HttpHeaders();
            String filename = formatarNomeArquivoRelatorio(turma);
            headers.add("Content-Disposition", "inline; filename=" + filename);

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private String formatarNomeArquivoRelatorio(Turma turma) {
        Professor professor = turma.getProfessor();
        String nomeProfessor = professor.getNome() + "_" + professor.getSobrenome();
        String nomeFormatado = nomeProfessor.replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();
        String codigoMateria = turma.getMateria().getCodigo().replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();
        String campus = turma.getCampus().replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();

        return "relacao_turma_" + nomeFormatado + "_" + codigoMateria + "_" + campus + ".pdf";
    }
}