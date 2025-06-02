package com.projeto.unify.services;

import com.projeto.unify.dtos.AvaliacaoDTO;
import com.projeto.unify.models.Avaliacao;
import com.projeto.unify.models.Professor;
import com.projeto.unify.models.Turma;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.repositories.AvaliacaoRepository;
import com.projeto.unify.repositories.ProfessorRepository;
import com.projeto.unify.repositories.TurmaRepository;
import com.projeto.unify.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository; 
    private final UsuarioRepository usuarioRepository; // To get current professor

    private Professor getProfessorLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado."));
        return professorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário logado não é um professor."));
    }

    @Transactional
    public Avaliacao criarAvaliacao(AvaliacaoDTO dto) {
        Professor professorLogado = getProfessorLogado();
        Turma turma = turmaRepository.findById(dto.getTurmaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada com ID: " + dto.getTurmaId()));

        // Validação: Professor só pode criar avaliação em suas turmas
        if (!turma.getProfessor().equals(professorLogado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Professor não tem permissão para criar avaliação nesta turma.");
        }

        Avaliacao avaliacao = Avaliacao.builder()
                .turma(turma)
                .nome(dto.getNome())
                .dataPrevista(dto.getDataPrevista())
                .valorMaximoPossivel(dto.getValorMaximoPossivel())
                .build();

        return avaliacaoRepository.save(avaliacao);
    }

    @Transactional(readOnly = true)
    public List<Avaliacao> listarAvaliacoesDaTurma(Long turmaId) {
        Professor professorLogado = getProfessorLogado();
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada com ID: " + turmaId));

        // Validação: Professor só pode listar avaliações de suas turmas (ou admin, etc. - simplificando para professor por agora)
        if (!turma.getProfessor().equals(professorLogado)) {
            // Poderia permitir que alunos da turma vejam também, ou admins.
            // Por enquanto, restrito ao professor da turma.
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não tem permissão para listar avaliações desta turma.");
        }
        return avaliacaoRepository.findByTurmaId(turmaId);
    }

    @Transactional(readOnly = true)
    public Avaliacao buscarAvaliacaoPorId(Long turmaId, Long avaliacaoId) {
        Professor professorLogado = getProfessorLogado();
        Avaliacao avaliacao = avaliacaoRepository.findByIdAndTurmaId(avaliacaoId, turmaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada com ID: " + avaliacaoId + " para a turma ID: " + turmaId));

        if (!avaliacao.getTurma().getProfessor().equals(professorLogado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não tem permissão para acessar esta avaliação.");
        }
        return avaliacao;
    }

    @Transactional
    public Avaliacao atualizarAvaliacao(Long avaliacaoId, AvaliacaoDTO dto) {
        Professor professorLogado = getProfessorLogado();
        Avaliacao avaliacaoExistente = avaliacaoRepository.findById(avaliacaoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada com ID: " + avaliacaoId));

        // Validação: Professor só pode atualizar avaliação em suas turmas
        if (!avaliacaoExistente.getTurma().getProfessor().equals(professorLogado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Professor não tem permissão para atualizar esta avaliação.");
        }
        // Validação: Turma não pode ser alterada na atualização por este método
        if (!avaliacaoExistente.getTurma().getId().equals(dto.getTurmaId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é permitido alterar a turma de uma avaliação existente.");
        }

        avaliacaoExistente.setNome(dto.getNome());
        avaliacaoExistente.setDataPrevista(dto.getDataPrevista());
        avaliacaoExistente.setValorMaximoPossivel(dto.getValorMaximoPossivel());

        return avaliacaoRepository.save(avaliacaoExistente);
    }

    @Transactional
    public void deletarAvaliacao(Long turmaId, Long avaliacaoId) {
        Professor professorLogado = getProfessorLogado();
        Avaliacao avaliacao = avaliacaoRepository.findByIdAndTurmaId(avaliacaoId, turmaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada para exclusão."));

        if (!avaliacao.getTurma().getProfessor().equals(professorLogado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Professor não tem permissão para deletar esta avaliação.");
        }

        // Adicionar lógica para verificar se existem notas lançadas antes de excluir, se necessário.
        // if (!avaliacao.getNotasLancadas().isEmpty()) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível excluir avaliação com notas já lançadas.");
        // }

        avaliacaoRepository.delete(avaliacao);
    }
} 