package com.projeto.unify.services;

import com.projeto.unify.dtos.solicitacoes.AlunoTransferenciaGraduacaoRequestDTO;
import com.projeto.unify.dtos.solicitacoes.ProcessarTransferenciaGraduacaoDTO;
import com.projeto.unify.dtos.solicitacoes.TransferenciaGraduacaoSolicitacaoResponseDTO;
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
public class TransferenciaGraduacaoSolicitacaoService {

    private final TransferenciaGraduacaoSolicitacaoRepository solicitacaoRepository;
    private final AlunoRepository alunoRepository;
    private final GraduacaoRepository graduacaoRepository;
    private final UsuarioService usuarioService;
    private final FuncionarioRepository funcionarioRepository;
    // TurmaRepository might be needed if we clear turma explicitly, AlunoService for matricula regen (future)

    private Aluno getAlunoLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);
        return alunoRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Aluno não encontrado ou não associado ao usuário logado."));
    }

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

    private TransferenciaGraduacaoSolicitacaoResponseDTO convertToDTO(TransferenciaGraduacaoSolicitacao solicitacao) {
        return TransferenciaGraduacaoSolicitacaoResponseDTO.builder()
                .id(solicitacao.getId())
                .alunoId(solicitacao.getAluno().getId())
                .alunoNome(solicitacao.getAluno().getNomeCompleto())
                .alunoMatricula(solicitacao.getAluno().getMatricula())
                .graduacaoAtualId(solicitacao.getGraduacaoAtual().getId())
                .graduacaoAtualNome(solicitacao.getGraduacaoAtual().getTitulo())
                .graduacaoAtualCodigo(solicitacao.getGraduacaoAtual().getCodigoCurso())
                .graduacaoDesejadaId(solicitacao.getGraduacaoDesejada().getId())
                .graduacaoDesejadaNome(solicitacao.getGraduacaoDesejada().getTitulo())
                .graduacaoDesejadaCodigo(solicitacao.getGraduacaoDesejada().getCodigoCurso())
                .justificativaAluno(solicitacao.getJustificativaAluno())
                .statusSolicitacao(solicitacao.getStatusSolicitacao())
                .dataSolicitacao(solicitacao.getDataSolicitacao())
                .dataProcessamento(solicitacao.getDataProcessamento())
                .observacaoSecretaria(solicitacao.getObservacaoSecretaria())
                .universidadeId(solicitacao.getUniversidade().getId())
                .universidadeNome(solicitacao.getUniversidade().getNome())
                .build();
    }

    // --- Methods for Aluno ---
    @Transactional
    public TransferenciaGraduacaoSolicitacaoResponseDTO criarSolicitacao(AlunoTransferenciaGraduacaoRequestDTO dto) {
        Aluno alunoLogado = getAlunoLogado();

        Graduacao graduacaoAtual = graduacaoRepository.findById(dto.getGraduacaoAtualId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Graduação atual não encontrada."));
        Graduacao graduacaoDesejada = graduacaoRepository.findById(dto.getGraduacaoDesejadaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Graduação desejada não encontrada."));

        if (alunoLogado.getGraduacao() == null || !alunoLogado.getGraduacao().equals(graduacaoAtual)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno não pertence à graduação atual informada.");
        }
        if (graduacaoAtual.equals(graduacaoDesejada)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Graduação atual e desejada são as mesmas.");
        }
        if (!graduacaoDesejada.getUniversidade().equals(alunoLogado.getUniversidade())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Graduação desejada não pertence à universidade do aluno.");
        }

        solicitacaoRepository.findByAlunoIdAndGraduacaoDesejadaIdAndStatusPendente(alunoLogado.getId(), graduacaoDesejada.getId())
            .ifPresent(s -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma solicitação de transferência pendente para esta graduação desejada."); });

        TransferenciaGraduacaoSolicitacao solicitacao = new TransferenciaGraduacaoSolicitacao();
        solicitacao.setAluno(alunoLogado);
        solicitacao.setGraduacaoAtual(graduacaoAtual);
        solicitacao.setGraduacaoDesejada(graduacaoDesejada);
        solicitacao.setJustificativaAluno(dto.getJustificativaAluno());
        // universidade and status are set in @PrePersist

        TransferenciaGraduacaoSolicitacao saved = solicitacaoRepository.save(solicitacao);
        return convertToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<TransferenciaGraduacaoSolicitacaoResponseDTO> listarMinhasSolicitacoes() {
        Aluno alunoLogado = getAlunoLogado();
        // It might be useful to list all, not just PENDENTE, so student sees history.
        // For now, sticking to PENDENTE to match TrocaTurma.
        return solicitacaoRepository.findByAlunoIdAndStatusSolicitacao(alunoLogado.getId(), StatusSolicitacao.PENDENTE)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // --- Methods for Secretaria ---
    @Transactional(readOnly = true)
    public List<TransferenciaGraduacaoSolicitacaoResponseDTO> listarSolicitacoesPendentesSecretaria() {
        Funcionario funcionario = getFuncionarioSecretariaLogado();
        Universidade universidade = getUniversidadeDoFuncionarioLogado(funcionario);
        return solicitacaoRepository.findByUniversidadeIdAndStatusSolicitacao(universidade.getId(), StatusSolicitacao.PENDENTE)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransferenciaGraduacaoSolicitacaoResponseDTO buscarSolicitacaoPorIdSecretaria(Long solicitacaoId) {
        Funcionario funcionario = getFuncionarioSecretariaLogado();
        Universidade universidade = getUniversidadeDoFuncionarioLogado(funcionario);
        TransferenciaGraduacaoSolicitacao solicitacao = solicitacaoRepository.findByIdAndUniversidadeId(solicitacaoId, universidade.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação de transferência não encontrada ou não pertence à sua universidade."));
        return convertToDTO(solicitacao);
    }

    @Transactional
    public TransferenciaGraduacaoSolicitacaoResponseDTO processarSolicitacao(Long solicitacaoId, ProcessarTransferenciaGraduacaoDTO dto) {
        Funcionario funcionario = getFuncionarioSecretariaLogado();
        Universidade universidade = getUniversidadeDoFuncionarioLogado(funcionario);

        TransferenciaGraduacaoSolicitacao solicitacao = solicitacaoRepository.findByIdAndUniversidadeId(solicitacaoId, universidade.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação de transferência não encontrada ou não pertence à sua universidade."));

        if (solicitacao.getStatusSolicitacao() != StatusSolicitacao.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta solicitação de transferência já foi processada: " + solicitacao.getStatusSolicitacao());
        }

        solicitacao.setStatusSolicitacao(dto.getNovaStatus());
        solicitacao.setObservacaoSecretaria(dto.getObservacaoSecretaria());
        solicitacao.setDataProcessamento(LocalDateTime.now());

        if (dto.getNovaStatus() == StatusSolicitacao.APROVADA) {
            Aluno aluno = solicitacao.getAluno();
            Graduacao novaGraduacao = solicitacao.getGraduacaoDesejada();

            // 1. Check if the new graduation still belongs to the same university (should be true from creation logic)
            if (!novaGraduacao.getUniversidade().equals(aluno.getUniversidade())){
                solicitacao.setStatusSolicitacao(StatusSolicitacao.REJEITADA);
                solicitacao.setObservacaoSecretaria((dto.getObservacaoSecretaria() == null ? "" : dto.getObservacaoSecretaria() + "\n") +
                         "Rejeitada automaticamente: Graduação desejada não pertence (ou deixou de pertencer) à universidade do aluno.");
            } else {
                // 2. Update student's graduation
                aluno.setGraduacao(novaGraduacao);
    
                // 3. Unenroll student from current class
                if (aluno.getTurma() != null) {
                    // Turma turmaAntiga = aluno.getTurma(); // Not needed to save turmaAntiga explicitly if Aluno is FK owner
                    aluno.setTurma(null);
                }
                
                // 4. Save the student with updated graduation and no class
                alunoRepository.save(aluno);
    
                // 5. Matricula regeneration is NOT handled here - manual process or future enhancement.
                // (Add warning to observacaoSecretaria?)
                String obsDefaultAprovacao = "Transferência de graduação aprovada. Aluno removido da turma anterior. Matrícula NÃO foi alterada automaticamente.";
                if (solicitacao.getObservacaoSecretaria() == null || solicitacao.getObservacaoSecretaria().isBlank()) {
                    solicitacao.setObservacaoSecretaria(obsDefaultAprovacao);
                } else {
                    solicitacao.setObservacaoSecretaria(solicitacao.getObservacaoSecretaria() + "\nLembrete: Matrícula NÃO foi alterada automaticamente.");
                }
            }
        } else if (dto.getNovaStatus() == StatusSolicitacao.REJEITADA) {
             if (solicitacao.getObservacaoSecretaria() == null || solicitacao.getObservacaoSecretaria().isBlank()) {
                 solicitacao.setObservacaoSecretaria("Solicitação de transferência de graduação rejeitada pela secretaria.");
            }
        }

        TransferenciaGraduacaoSolicitacao saved = solicitacaoRepository.save(solicitacao);
        return convertToDTO(saved);
    }
} 