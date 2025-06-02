package com.projeto.unify.services;

import com.projeto.unify.dtos.NotaDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotaService {

    private final NotaRepository notaRepository;
    private final AlunoRepository alunoRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final ProfessorRepository professorRepository;
    private final UsuarioRepository usuarioRepository;
    private final TurmaRepository turmaRepository; // Para buscar a turma e verificar o professor
    private final MateriaRepository materiaRepository; // Para buscar a nota mínima
    private final UsuarioService usuarioService; // Adicionado para buscar usuário logado

    private Professor getProfessorLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(emailUsuarioLogado); // Usando UsuarioService
        return professorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário logado não é um professor."));
    }

    private Aluno getAlunoLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(emailUsuarioLogado); // Usando UsuarioService
        return alunoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário logado não é um aluno ou não encontrado."));
    }

    @Transactional
    public Nota lancarOuAtualizarNota(NotaDTO dto) {
        Professor professorLogado = getProfessorLogado();

        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado com ID: " + dto.getAlunoId()));

        Avaliacao avaliacao = avaliacaoRepository.findById(dto.getAvaliacaoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada com ID: " + dto.getAvaliacaoId()));

        // Validação: Professor só pode lançar nota em avaliação de suas turmas
        if (!avaliacao.getTurma().getProfessor().equals(professorLogado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Professor não tem permissão para lançar/atualizar nota nesta avaliação.");
        }

        // Validação: Aluno deve pertencer à turma da avaliação
        if (aluno.getTurma() == null || !aluno.getTurma().equals(avaliacao.getTurma())) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno não pertence à turma desta avaliação.");
        }

        // Validação: Nota não pode exceder valorMaximoPossivel da Avaliacao (se definido)
        if (avaliacao.getValorMaximoPossivel() != null && dto.getValorObtido() > avaliacao.getValorMaximoPossivel()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "A nota obtida (" + dto.getValorObtido() + ") não pode exceder o valor máximo possível da avaliação (" + avaliacao.getValorMaximoPossivel() + ").");
        }

        Optional<Nota> notaExistenteOpt = notaRepository.findByAlunoAndAvaliacao(aluno, avaliacao);

        Nota nota;
        if (notaExistenteOpt.isPresent()) {
            nota = notaExistenteOpt.get();
            nota.setValorObtido(dto.getValorObtido());
            nota.setObservacoes(dto.getObservacoes());
            // dataLancamento será atualizada automaticamente pelo @PreUpdate se definido ou manualmente aqui
        } else {
            nota = Nota.builder()
                    .aluno(aluno)
                    .avaliacao(avaliacao)
                    .valorObtido(dto.getValorObtido())
                    .observacoes(dto.getObservacoes())
                    // dataLancamento será definida pelo @PrePersist
                    .build();
        }
        return notaRepository.save(nota);
    }

    @Transactional(readOnly = true)
    public List<Nota> listarNotasDaAvaliacao(Long avaliacaoId) {
        // Permitir que professor da turma ou admin vejam.
        Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada."));
        
        // Validação básica de segurança (professor da turma)
        Professor professorLogado = getProfessorLogado();
        if (avaliacao.getTurma() == null || avaliacao.getTurma().getProfessor() == null || !avaliacao.getTurma().getProfessor().equals(professorLogado)) {
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não tem permissão para listar notas desta avaliação.");
        }

        return notaRepository.findByAvaliacao(avaliacao);
    }

    @Transactional(readOnly = true)
    public List<Nota> listarNotasDoAlunoLogadoNaTurma(Long turmaId) {
        Aluno alunoLogado = getAlunoLogado();
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada."));

        if (alunoLogado.getTurma() == null || !alunoLogado.getTurma().equals(turma)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno não está matriculado nesta turma.");
        }
        return notaRepository.findAllByAlunoIdAndTurmaId(alunoLogado.getId(), turmaId);
    }

    @Transactional(readOnly = true)
    public List<Nota> listarNotasDoAlunoNaTurma(Long alunoId, Long turmaId) {
        // Esta versão é para o professor ou admin verem as notas de um aluno específico.
        // A segurança de quem pode chamar isso (professor da turma, admin) é feita no controller.
        // getProfessorLogado(); // Comentado pois a lógica de permissão pode ser mais complexa e feita no controller

        Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada."));

        if(aluno.getTurma() == null || !aluno.getTurma().equals(turma)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno não está matriculado nesta turma.");
        }

        return notaRepository.findAllByAlunoIdAndTurmaId(alunoId, turmaId);
    }

    @Transactional(readOnly = true)
    public Double calcularMediaFinalAlunoLogadoNaTurma(Long turmaId) {
        Aluno alunoLogado = getAlunoLogado();
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada."));

        if (alunoLogado.getTurma() == null || !alunoLogado.getTurma().equals(turma)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno não está matriculado nesta turma.");
        }

        if (turma.getAvaliacoesDefinidas() == null || turma.getAvaliacoesDefinidas().isEmpty()) {
            return 0.0;
        }
        return notaRepository.findAverageNotaByAlunoAndTurma(alunoLogado.getId(), turmaId);
    }

    @Transactional(readOnly = true)
    public Double calcularMediaFinalAlunoNaTurma(Long alunoId, Long turmaId) {
        // Esta versão é para o professor ou admin.
        // getProfessorLogado(); // Comentado pois a lógica de permissão pode ser mais complexa e feita no controller

         Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada."));

        if(aluno.getTurma() == null || !aluno.getTurma().equals(turma)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno não está matriculado nesta turma.");
        }
        
        // Garante que a turma tem avaliações definidas
        if (turma.getAvaliacoesDefinidas() == null || turma.getAvaliacoesDefinidas().isEmpty()) {
            // Ou retorna 0.0, ou lança exceção, dependendo da regra de negócio
            return 0.0; 
        }

        return notaRepository.findAverageNotaByAlunoAndTurma(alunoId, turmaId);
    }

    @Transactional(readOnly = true)
    public String verificarStatusAprovacaoAlunoLogado(Long turmaId) {
        Aluno alunoLogado = getAlunoLogado();
        Double media = calcularMediaFinalAlunoLogadoNaTurma(turmaId); // Reutiliza o método que já valida a turma do aluno
        
        // calcularMediaFinalAlunoLogadoNaTurma já retorna 0.0 se não houver avaliações.
        // Se a média for null do repositório (sem notas para avaliações existentes), o `findAverageNotaByAlunoAndTurma` retorna null.
        // Precisamos tratar o null explicitamente aqui antes de comparar.

        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada."));
        Materia materia = turma.getMateria();

        if (materia.getNotaMinimaAprovacao() == null) {
            return "Nota mínima para aprovação não definida para esta matéria.";
        }
        
        if (media == null) { // Caso não haja notas lançadas para o aluno nas avaliações da turma
            return "Reprovado (Nenhuma nota lançada)"; // Ou "Notas pendentes"
        }

        if (media >= materia.getNotaMinimaAprovacao()) {
            return "Aprovado (Média: " + String.format("%.2f", media) + ")";
        } else {
            return "Reprovado (Média: " + String.format("%.2f", media) + ")";
        }
    }

    @Transactional(readOnly = true)
    public String verificarStatusAprovacao(Long alunoId, Long turmaId) {
        // Esta versão é para o professor ou admin.
        // getProfessorLogado(); // Comentado pois a lógica de permissão pode ser mais complexa e feita no controller

        Double media = calcularMediaFinalAlunoNaTurma(alunoId, turmaId);
        if (media == null) {
            return "Média ainda não calculada (sem notas ou sem avaliações).";
        }

        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada."));
        Materia materia = turma.getMateria();
        
        if (materia.getNotaMinimaAprovacao() == null) {
            return "Nota mínima para aprovação não definida para esta matéria.";
        }

        if (media >= materia.getNotaMinimaAprovacao()) {
            return "Aprovado (Média: " + String.format("%.2f", media) + ")";
        } else {
            return "Reprovado (Média: " + String.format("%.2f", media) + ")";
        }
    }
} 