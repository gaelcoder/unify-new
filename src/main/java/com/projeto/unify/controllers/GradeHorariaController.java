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
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/grade-horaria")
@RequiredArgsConstructor
public class GradeHorariaController {

    private final PdfService pdfService;
    private final AlunoService alunoService;
    private final UsuarioService usuarioService;

    @PostMapping("/gerar")
    public ResponseEntity<InputStreamResource> gerarGradeHoraria(@RequestBody Map<String, Long> payload) {
        try {
            Long usuarioId = payload.get("alunoId"); // As per original logic, this is the usuarioId
            Usuario usuario = usuarioService.findById(usuarioId);
            Aluno aluno = alunoService.buscarPorUsuario(usuario);

            ByteArrayInputStream bis = pdfService.gerarGradeHorariaPdf(aluno);

            HttpHeaders headers = new HttpHeaders();
            String filename = formatarNomeArquivoGrade(aluno);
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

    private String formatarNomeArquivoGrade(Aluno aluno) {
        String nomeCompleto = aluno.getNome() + "_" + aluno.getSobrenome();
        String nomeFormatado = nomeCompleto.replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();
        return "grade_horaria_" + nomeFormatado + "_" + aluno.getMatricula() + ".pdf";
    }
} 