package com.projeto.unify.services;

import com.projeto.unify.dtos.solicitacoes.AlunoTrocaTurmaRequestDTO;
import com.projeto.unify.dtos.solicitacoes.ProcessarTrocaTurmaDTO;
import com.projeto.unify.dtos.solicitacoes.TrocaTurmaSolicitacaoResponseDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.models.enums.StatusSolicitacao;
import com.projeto.unify.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrocaTurmaSolicitacaoService {

    private final TrocaTurmaSolicitacaoRepository solicitacaoRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final UsuarioService usuarioService;
    private final FuncionarioRepository funcionarioRepository;
    private final TurmaService turmaService; // For actual enrollment

    // Helper to get logged-in Aluno
    private Aluno getAlunoLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);
        return alunoRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Aluno não encontrado ou não associado ao usuário logado."));
    }

    // Helper to get logged-in Funcionario (Secretaria)
    private Funcionario getFuncionarioSecretariaLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);
        return funcionarioRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário da secretaria não encontrado."));
    }

    private Universidade getUniversidadeDoFuncionarioLogado(Funcionario funcionario) {
        Universidade universidade = funcionario.getUniversidade();
        if (universidade == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Funcionário não está associado a nenhuma universidade.");
        }
        return universidade;
    }

    private TrocaTurmaSolicitacaoResponseDTO convertToDTO(TrocaTurmaSolicitacao solicitacao) {
        return TrocaTurmaSolicitacaoResponseDTO.builder()
                .id(solicitacao.getId())
                .alunoId(solicitacao.getAluno().getId())
                .alunoNome(solicitacao.getAluno().getNomeCompleto())
                .alunoMatricula(solicitacao.getAluno().getMatricula())
                .turmaAtualId(solicitacao.getTurmaAtual().getId())
                .turmaAtualDescricao(formatTurmaDescricao(solicitacao.getTurmaAtual()))
                .turmaDesejadaId(solicitacao.getTurmaDesejada().getId())
                .turmaDesejadaDescricao(formatTurmaDescricao(solicitacao.getTurmaDesejada()))
                .justificativaAluno(solicitacao.getJustificativaAluno())
                .statusSolicitacao(solicitacao.getStatusSolicitacao())
                .dataSolicitacao(solicitacao.getDataSolicitacao())
                .dataProcessamento(solicitacao.getDataProcessamento())
                .observacaoSecretaria(solicitacao.getObservacaoSecretaria())
                .universidadeId(solicitacao.getUniversidade().getId())
                .universidadeNome(solicitacao.getUniversidade().getNome())
                .build();
    }

    private String formatTurmaDescricao(Turma turma) {
        if (turma == null) return "N/A";
        return String.format("%s - %s (%s) - %s",
                turma.getMateria().getTitulo(),
                turma.getGraduacao().getTitulo(),
                turma.getGraduacao().getCodigoCurso(),
                turma.getTurno());
    }

    // --- Methods for Aluno ---
    @Transactional
    public TrocaTurmaSolicitacaoResponseDTO criarSolicitacao(AlunoTrocaTurmaRequestDTO dto) {
        Aluno alunoLogado = getAlunoLogado();

        Turma turmaAtual = turmaRepository.findById(dto.getTurmaAtualId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma atual não encontrada."));
        Turma turmaDesejada = turmaRepository.findById(dto.getTurmaDesejadaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma desejada não encontrada."));

        if (!alunoLogado.getTurma().equals(turmaAtual)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno não pertence à turma atual informada.");
        }
        if (turmaAtual.equals(turmaDesejada)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Turma atual e desejada são as mesmas.");
        }
        if (!turmaAtual.getGraduacao().equals(turmaDesejada.getGraduacao())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A troca só pode ser feita entre turmas da mesma graduação.");
        }
         if (!turmaDesejada.getGraduacao().getUniversidade().equals(alunoLogado.getUniversidade())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Turma desejada não pertence à universidade do aluno.");
        }

        // Check for existing pending request for the same desired class
        solicitacaoRepository.findByAlunoIdAndTurmaDesejadaIdAndStatusPendente(alunoLogado.getId(), turmaDesejada.getId())
            .ifPresent(s -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma solicitação pendente para esta turma desejada."); });

        if (turmaDesejada.getAlunos().size() >= turmaDesejada.getLimiteAlunos()){
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Turma desejada está cheia.");
        }

        TrocaTurmaSolicitacao solicitacao = new TrocaTurmaSolicitacao();
        solicitacao.setAluno(alunoLogado);
        solicitacao.setTurmaAtual(turmaAtual);
        solicitacao.setTurmaDesejada(turmaDesejada);
        solicitacao.setJustificativaAluno(dto.getJustificativaAluno());
        // universidade and status are set in @PrePersist

        TrocaTurmaSolicitacao saved = solicitacaoRepository.save(solicitacao);
        return convertToDTO(saved);
    }
    
    @Transactional(readOnly = true)
    public List<TrocaTurmaSolicitacaoResponseDTO> listarMinhasSolicitacoes() {
        Aluno alunoLogado = getAlunoLogado();
        return solicitacaoRepository.findByAlunoIdAndStatusSolicitacao(alunoLogado.getId(), StatusSolicitacao.PENDENTE)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // --- Methods for Secretaria ---
    @Transactional(readOnly = true)
    public List<TrocaTurmaSolicitacaoResponseDTO> listarSolicitacoesPendentesSecretaria() {
        Funcionario funcionario = getFuncionarioSecretariaLogado();
        Universidade universidade = getUniversidadeDoFuncionarioLogado(funcionario);
        return solicitacaoRepository.findByUniversidadeIdAndStatusSolicitacao(universidade.getId(), StatusSolicitacao.PENDENTE)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TrocaTurmaSolicitacaoResponseDTO buscarSolicitacaoPorIdSecretaria(Long solicitacaoId) {
        Funcionario funcionario = getFuncionarioSecretariaLogado();
        Universidade universidade = getUniversidadeDoFuncionarioLogado(funcionario);
        TrocaTurmaSolicitacao solicitacao = solicitacaoRepository.findByIdAndUniversidadeId(solicitacaoId, universidade.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação não encontrada ou não pertence à sua universidade."));
        return convertToDTO(solicitacao);
    }

    @Transactional
    public TrocaTurmaSolicitacaoResponseDTO processarSolicitacao(Long solicitacaoId, ProcessarTrocaTurmaDTO dto) {
        Funcionario funcionario = getFuncionarioSecretariaLogado();
        Universidade universidade = getUniversidadeDoFuncionarioLogado(funcionario);

        TrocaTurmaSolicitacao solicitacao = solicitacaoRepository.findByIdAndUniversidadeId(solicitacaoId, universidade.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação não encontrada ou não pertence à sua universidade."));

        if (solicitacao.getStatusSolicitacao() != StatusSolicitacao.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta solicitação já foi processada: " + solicitacao.getStatusSolicitacao());
        }

        solicitacao.setStatusSolicitacao(dto.getNovaStatus());
        solicitacao.setObservacaoSecretaria(dto.getObservacaoSecretaria());
        solicitacao.setDataProcessamento(LocalDateTime.now());

        if (dto.getNovaStatus() == StatusSolicitacao.APROVADA) {
            // Check if desired class still has capacity
            if (solicitacao.getTurmaDesejada().getAlunos().size() >= solicitacao.getTurmaDesejada().getLimiteAlunos()) {
                solicitacao.setStatusSolicitacao(StatusSolicitacao.REJEITADA); // Auto-reject if full
                solicitacao.setObservacaoSecretaria((dto.getObservacaoSecretaria() == null ? "" : dto.getObservacaoSecretaria() + "\n") + "Rejeitada automaticamente: Turma desejada ficou cheia.");
            } else {
                try {
                    turmaService.matricularAluno(solicitacao.getTurmaDesejada().getId(), solicitacao.getAluno().getId());
                    // Success, original observation stands or add a default one if null
                     if (solicitacao.getObservacaoSecretaria() == null || solicitacao.getObservacaoSecretaria().isBlank()) {
                        solicitacao.setObservacaoSecretaria("Troca de turma aprovada e efetivada.");
                    }
                } catch (ResponseStatusException e) {
                    // If matricularAluno fails (e.g. student no longer valid, or some other constraint)
                    solicitacao.setStatusSolicitacao(StatusSolicitacao.REJEITADA);
                    solicitacao.setObservacaoSecretaria((dto.getObservacaoSecretaria() == null ? "" : dto.getObservacaoSecretaria() + "\n") +
                            "Falha ao efetivar a troca: " + e.getReason());
                } catch (Exception e) {
                     solicitacao.setStatusSolicitacao(StatusSolicitacao.REJEITADA);
                    solicitacao.setObservacaoSecretaria((dto.getObservacaoSecretaria() == null ? "" : dto.getObservacaoSecretaria() + "\n") +
                            "Erro inesperado ao efetivar a troca: " + e.getMessage());
                }
            }
        } else if (dto.getNovaStatus() == StatusSolicitacao.REJEITADA) {
            if (solicitacao.getObservacaoSecretaria() == null || solicitacao.getObservacaoSecretaria().isBlank()) {
                 solicitacao.setObservacaoSecretaria("Solicitação de troca de turma rejeitada pela secretaria.");
            }
        }

        TrocaTurmaSolicitacao saved = solicitacaoRepository.save(solicitacao);
        return convertToDTO(saved);
    }
} 