package com.projeto.unify.services;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.svg.converter.SvgConverter;
import com.projeto.unify.models.Aluno;
import com.projeto.unify.models.Professor;
import com.projeto.unify.models.Turma;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final TurmaService turmaService;

    public ByteArrayInputStream gerarGradeHorariaPdf(Aluno aluno) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        addCabecalho(document, aluno, pdfDoc);
        addInfoAluno(document, aluno);
        addTabelaHorarios(document, aluno);
        addRodape(document);

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addCabecalho(Document document, Aluno aluno, PdfDocument pdfDoc) throws IOException {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 4})).useAllAvailableWidth();
        table.setBorder(null);

        try {
            String rootPath = System.getProperty("user.dir");
            String logoFilename = aluno.getUniversidade().getLogoPath();
            if (logoFilename != null && !logoFilename.isEmpty()) {
                String logoPath = Paths.get(rootPath, "upload-dir", logoFilename).toString();
                byte[] fileContent = Files.readAllBytes(Paths.get(logoPath));

                Image logo;
                // Verificação robusta para SVG
                String contentSample = new String(fileContent, 0, Math.min(fileContent.length, 100)).trim().toLowerCase();
                if (contentSample.contains("<svg") || logoFilename.trim().toLowerCase().endsWith(".svg")) {
                    logo = SvgConverter.convertToImage(new ByteArrayInputStream(fileContent), pdfDoc);
                } else {
                    logo = new Image(ImageDataFactory.create(fileContent));
                }

                logo.scaleToFit(100, 100);
                Cell logoCell = new Cell().add(logo).setBorder(null);
                table.addCell(logoCell);
            } else {
                table.addCell(new Cell().add(new Paragraph("Logo indisponível")).setBorder(null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            table.addCell(new Cell().add(new Paragraph("Logo indisponível")).setBorder(null));
        }

        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        Paragraph nomeUniversidade = new Paragraph(aluno.getUniversidade().getNome()).setFont(bold).setFontSize(18);
        Cell nomeUniversidadeCell = new Cell().add(nomeUniversidade).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(null);
        table.addCell(nomeUniversidadeCell);

        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private void addInfoAluno(Document document, Aluno aluno) throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        String info = String.format("Aluno: %s | Matrícula: %s | CPF: %s | Curso: %s | Campus: %s",
                aluno.getUsuario().getNome(),
                aluno.getMatricula(),
                aluno.getCpf(),
                aluno.getGraduacao().getTitulo(),
                aluno.getCampus());
        document.add(new Paragraph(info).setFont(font).setFontSize(10));
        document.add(new Paragraph("\n"));
    }

    private void addTabelaHorarios(Document document, Aluno aluno) throws IOException {
        Table table = new Table(UnitValue.createPercentArray(6)).useAllAvailableWidth();

        String[] cabecalho = {"Turno", "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira"};
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        for (String header : cabecalho) {
            table.addHeaderCell(new Cell().add(new Paragraph(header).setFont(boldFont)));
        }

        String[] turnos = {"MANHA", "TARDE", "NOITE"};
        String[] turnosDisplay = {"Manhã", "Tarde", "Noite"};
        String[] dias = {"SEGUNDA", "TERCA", "QUARTA", "QUINTA", "SEXTA"};
        List<Turma> turmas = turmaService.findAllByAlunoId(aluno.getId());

        PdfFont cellFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        for (int i = 0; i < turnos.length; i++) {
            table.addCell(new Cell().add(new Paragraph(turnosDisplay[i]).setFont(cellFont)));
            for (String dia : dias) {
                StringBuilder infoCela = new StringBuilder();
                for (Turma turma : turmas) {
                    if (turma.getTurno() != null && turma.getDiaSemana() != null &&
                            turma.getTurno().equalsIgnoreCase(turnos[i]) &&
                            turma.getDiaSemana().equalsIgnoreCase(dia)) {

                        if (turma.getMateria() != null && turma.getProfessor() != null) {
                            if (infoCela.length() > 0) {
                                infoCela.append("\n\n");
                            }
                            infoCela.append(String.format("%s\n%s\nProf: %s",
                                    turma.getMateria().getTitulo(),
                                    turma.getMateria().getCodigo(),
                                    turma.getProfessor().getNome()));
                        }
                    }
                }
                table.addCell(new Cell().add(new Paragraph(infoCela.toString()).setFont(cellFont).setFontSize(8)));
            }
        }
        document.add(table);
    }

    private void addRodape(Document document) throws IOException {
        document.add(new Paragraph("\n"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 4})).useAllAvailableWidth();
        table.setBorder(null);

        // Coluna do Logo Unify
        try {
            String rootPath = System.getProperty("user.dir");
            String logoPath = Paths.get(rootPath, "logos-unify", "logo_unify.png").toString();
            Image logo = new Image(ImageDataFactory.create(logoPath));
            logo.scaleToFit(50, 50);
            Cell logoCell = new Cell().add(logo).setBorder(null).setVerticalAlignment(VerticalAlignment.MIDDLE);
            table.addCell(logoCell);
        } catch (Exception e) {
            e.printStackTrace();
            table.addCell(new Cell().add(new Paragraph("")).setBorder(null));
        }

        // Coluna do Texto
        PdfFont italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String dataGeracao = dtf.format(now);

        Paragraph p = new Paragraph("Documento gerado com o sistema Unify em " + dataGeracao)
                .setFont(italic)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER);

        Cell textCell = new Cell().add(p).setBorder(null).setVerticalAlignment(VerticalAlignment.MIDDLE);
        table.addCell(textCell);

        document.add(table);
    }

    public ByteArrayInputStream gerarRelatorioTurmaPdf(Turma turma) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        addCabecalhoRelatorio(document, turma, pdfDoc);
        addInfoProfessorETurma(document, turma);
        addListaAlunos(document, turma);
        addRodape(document);

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addCabecalhoRelatorio(Document document, Turma turma, PdfDocument pdfDoc) throws IOException {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 4})).useAllAvailableWidth();
        table.setBorder(null);

        try {
            String rootPath = System.getProperty("user.dir");
            String logoFilename = turma.getProfessor().getUniversidade().getLogoPath();
            if (logoFilename != null && !logoFilename.isEmpty()) {
                String logoPath = Paths.get(rootPath, "upload-dir", logoFilename).toString();
                byte[] fileContent = Files.readAllBytes(Paths.get(logoPath));

                Image logo;
                String contentSample = new String(fileContent, 0, Math.min(fileContent.length, 100)).trim().toLowerCase();
                if (contentSample.contains("<svg") || logoFilename.trim().toLowerCase().endsWith(".svg")) {
                    logo = SvgConverter.convertToImage(new ByteArrayInputStream(fileContent), pdfDoc);
                } else {
                    logo = new Image(ImageDataFactory.create(fileContent));
                }

                logo.scaleToFit(100, 100);
                Cell logoCell = new Cell().add(logo).setBorder(null);
                table.addCell(logoCell);
            } else {
                table.addCell(new Cell().add(new Paragraph("Logo indisponível")).setBorder(null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            table.addCell(new Cell().add(new Paragraph("Logo indisponível")).setBorder(null));
        }

        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        Paragraph nomeUniversidade = new Paragraph(turma.getProfessor().getUniversidade().getNome()).setFont(bold).setFontSize(18);
        Cell nomeUniversidadeCell = new Cell().add(nomeUniversidade).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(null);
        table.addCell(nomeUniversidadeCell);

        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private void addInfoProfessorETurma(Document document, Turma turma) throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Professor professor = turma.getProfessor();

        String infoProfessor = String.format("Professor: %s", professor.getNomeCompleto());
        document.add(new Paragraph(infoProfessor).setFont(font).setFontSize(10));

        String infoTurma = String.format("Turma: %d - %s | Campus: %s | Turno: %s | Dia: %s",
                turma.getId(),
                turma.getMateria().getTitulo(),
                turma.getCampus(),
                formatarTurno(turma.getTurno()),
                formatarDiaSemana(turma.getDiaSemana())
        );
        document.add(new Paragraph(infoTurma).setFont(font).setFontSize(10));
        document.add(new Paragraph("\n"));
    }

    private void addListaAlunos(Document document, Turma turma) throws IOException {
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1})).useAllAvailableWidth();

        String[] cabecalho = {"Nome Completo", "Matrícula"};
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        for (String header : cabecalho) {
            table.addHeaderCell(new Cell().add(new Paragraph(header).setFont(boldFont)));
        }

        PdfFont cellFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        for (Aluno aluno : turma.getAlunos()) {
            table.addCell(new Cell().add(new Paragraph(aluno.getNomeCompleto()).setFont(cellFont).setFontSize(9)));
            table.addCell(new Cell().add(new Paragraph(aluno.getMatricula()).setFont(cellFont).setFontSize(9)));
        }
        document.add(table);
    }

    private String formatarTurno(String turno) {
        if (turno == null) return "";
        return switch (turno.toUpperCase()) {
            case "MANHA" -> "Manhã";
            case "TARDE" -> "Tarde";
            case "NOITE" -> "Noite";
            default -> turno;
        };
    }

    private String formatarDiaSemana(String diaSemana) {
        if (diaSemana == null) return "";
        return switch (diaSemana.toUpperCase()) {
            case "SEGUNDA" -> "Segunda-feira";
            case "TERCA" -> "Terça-feira";
            case "QUARTA" -> "Quarta-feira";
            case "QUINTA" -> "Quinta-feira";
            case "SEXTA" -> "Sexta-feira";
            default -> diaSemana;
        };
    }
}