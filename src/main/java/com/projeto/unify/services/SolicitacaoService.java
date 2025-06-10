package com.projeto.unify.services;

import com.projeto.unify.dtos.SolicitacaoDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.repositories.AlunoRepository;
import com.projeto.unify.repositories.SolicitacaoRepository;
import com.projeto.unify.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SolicitacaoService {

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Transactional
    public Solicitacao create(SolicitacaoDTO solicitacaoDTO) {
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

        return solicitacaoRepository.save(solicitacao);
    }
} 