package com.projeto.unify.services;

import com.projeto.unify.dtos.SolicitacaoCreateDTO;
import com.projeto.unify.dtos.SolicitacaoResponseDTO;
import com.projeto.unify.dtos.SolicitacaoSecretariaDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.repositories.AlunoRepository;
import com.projeto.unify.repositories.SolicitacaoRepository;
import com.projeto.unify.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SolicitacaoService {

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private UsuarioService usuarioService;

    public List<Solicitacao> findByAlunoId(Long alunoId) {
        return solicitacaoRepository.findByAlunoId(alunoId);
    }

    public List<SolicitacaoResponseDTO> findDTOByAlunoId(Long alunoId) {
        return solicitacaoRepository.findByAlunoId(alunoId).stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    private SolicitacaoResponseDTO toResponseDTO(Solicitacao solicitacao) {
        return new SolicitacaoResponseDTO(
                solicitacao.getId(),
                solicitacao.getTipo(),
                solicitacao.getMensagem(),
                solicitacao.getStatus(),
                solicitacao.getDataSolicitacao().toString()
        );
    }

    public List<SolicitacaoSecretariaDTO> findBySecretaria(Long idUniversidade, Optional<String> campus, Solicitacao.StatusSolicitacao status) {
        List<Solicitacao> solicitacoes;
        if (campus.isPresent()) {
            solicitacoes = solicitacaoRepository.findByUniversidadeIdAndAluno_CampusAndStatus(idUniversidade, campus.get(), status);
        } else {
            solicitacoes = solicitacaoRepository.findByUniversidadeIdAndStatus(idUniversidade, status);
        }
        return solicitacoes.stream()
                .map(this::toSecretariaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SolicitacaoResponseDTO updateStatus(Long id, Solicitacao.StatusSolicitacao status) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
        solicitacao.setStatus(status);
        Solicitacao updatedSolicitacao = solicitacaoRepository.save(solicitacao);
        return toResponseDTO(updatedSolicitacao);
    }

    @Transactional
    public SolicitacaoResponseDTO create(SolicitacaoCreateDTO solicitacaoDTO) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.findByEmail(userEmail);

        Aluno aluno = alunoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setTipo(solicitacaoDTO.tipo());
        solicitacao.setMensagem(solicitacaoDTO.mensagem());
        solicitacao.setAluno(aluno);
        solicitacao.setUniversidade(aluno.getUniversidade());
        solicitacao.setStatus(Solicitacao.StatusSolicitacao.ABERTA);
        solicitacao.setDataSolicitacao(LocalDateTime.now());

        Solicitacao savedSolicitacao = solicitacaoRepository.save(solicitacao);
        return toResponseDTO(savedSolicitacao);
    }

    private SolicitacaoSecretariaDTO toSecretariaDTO(Solicitacao solicitacao) {
        return new SolicitacaoSecretariaDTO(
                solicitacao.getId(),
                generateProtocolo(solicitacao),
                solicitacao.getAluno().getMatricula(),
                solicitacao.getAluno().getUsuario().getNome(),
                solicitacao.getTipo(),
                solicitacao.getAluno().getCampus()
        );
    }

    private String generateProtocolo(Solicitacao solicitacao) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dataFormatada = solicitacao.getDataSolicitacao().format(formatter);
        return dataFormatada + "-" + solicitacao.getId();
    }
} 