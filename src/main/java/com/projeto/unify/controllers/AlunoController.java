package com.projeto.unify.controllers;

import com.projeto.unify.models.Aluno;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.services.AlunoService;
import com.projeto.unify.services.PdfService;
import com.projeto.unify.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/aluno")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ALUNO')")
public class AlunoController {

    private final AlunoService alunoService;
    private final UsuarioService usuarioService;
    private final PdfService pdfService;

    @GetMapping("/grade-horaria/pdf")
    public ResponseEntity<InputStreamResource> gerarGradeHorariaPdf(Principal principal) {
        try {
            String userEmail = principal.getName();
            Usuario usuario = usuarioService.findByEmail(userEmail);
            Aluno aluno = alunoService.buscarPorUsuario(usuario);

            ByteArrayInputStream bis = pdfService.gerarGradeHorariaPdf(aluno);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=grade_horaria.pdf");

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
}
