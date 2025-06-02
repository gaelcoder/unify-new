package com.projeto.unify.services;

import com.projeto.unify.dtos.GraduacaoDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.repositories.FuncionarioRepository;
import com.projeto.unify.repositories.GraduacaoRepository;
import com.projeto.unify.repositories.ProfessorRepository;
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
public class GraduacaoService {

    private final GraduacaoRepository graduacaoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioService usuarioService;
    private final ProfessorRepository professorRepository; // To fetch professor for coordenador

    private Funcionario getFuncionarioSecretariaLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);
        return funcionarioRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário da secretaria não encontrado ou não associado ao usuário logado."));
    }

    private Universidade getUniversidadeDoFuncionarioLogado() {
        Funcionario funcionarioLogado = getFuncionarioSecretariaLogado();
        Universidade universidade = funcionarioLogado.getUniversidade();
        if (universidade == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Funcionário não está associado a nenhuma universidade.");
        }
        return universidade;
    }

    @Transactional
    public Graduacao criar(GraduacaoDTO graduacaoDTO) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();

        // Check for existing Graduacao with the same codigoCurso in the same university
        if (graduacaoRepository.existsByCodigoCursoAndUniversidadeId(graduacaoDTO.getCodigoCurso(), universidade.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Já existe uma graduação com o código '" + graduacaoDTO.getCodigoCurso() + "' nesta universidade.");
        }

        Graduacao novaGraduacao = new Graduacao();
        novaGraduacao.setTitulo(graduacaoDTO.getTitulo());
        novaGraduacao.setSemestres(graduacaoDTO.getSemestres());
        novaGraduacao.setCodigoCurso(graduacaoDTO.getCodigoCurso());
        novaGraduacao.setCampusDisponivel(graduacaoDTO.getCampusDisponivel());
        novaGraduacao.setUniversidade(universidade);

        if (graduacaoDTO.getCoordenadorDoCursoId() != null) {
            Professor coordenador = professorRepository.findById(graduacaoDTO.getCoordenadorDoCursoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Professor (coordenador) com ID " + graduacaoDTO.getCoordenadorDoCursoId() + " não encontrado."));
            if (!coordenador.getUniversidade().equals(universidade)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Professor (coordenador) não pertence à mesma universidade da graduação.");
            }
            novaGraduacao.setCoordenadorDoCurso(coordenador);
        }

        return graduacaoRepository.save(novaGraduacao);
    }

    @Transactional(readOnly = true)
    public List<Graduacao> listarGraduacoesDaUniversidadeLogada() {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        return graduacaoRepository.findByUniversidadeId(universidade.getId());
    }

    @Transactional(readOnly = true)
    public Graduacao buscarGraduacaoPorIdDaUniversidadeLogada(Long graduacaoId) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Graduacao graduacao = graduacaoRepository.findById(graduacaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Graduação não encontrada com ID: " + graduacaoId));

        if (!graduacao.getUniversidade().equals(universidade)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Graduação não pertence à universidade do funcionário.");
        }
        return graduacao;
    }

    @Transactional
    public Graduacao atualizarGraduacao(Long graduacaoId, GraduacaoDTO graduacaoDTO) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Graduacao graduacaoExistente = buscarGraduacaoPorIdDaUniversidadeLogada(graduacaoId); // Already validates ownership

        // Check for codigoCurso uniqueness if it's being changed
        if (!graduacaoExistente.getCodigoCurso().equals(graduacaoDTO.getCodigoCurso())) {
            if (graduacaoRepository.existsByCodigoCursoAndUniversidadeIdAndIdNot(graduacaoDTO.getCodigoCurso(), universidade.getId(), graduacaoId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Já existe outra graduação com o código '" + graduacaoDTO.getCodigoCurso() + "' nesta universidade.");
            }
            graduacaoExistente.setCodigoCurso(graduacaoDTO.getCodigoCurso());
        }

        graduacaoExistente.setTitulo(graduacaoDTO.getTitulo());
        graduacaoExistente.setSemestres(graduacaoDTO.getSemestres());
        graduacaoExistente.setCampusDisponivel(graduacaoDTO.getCampusDisponivel());

        if (graduacaoDTO.getCoordenadorDoCursoId() != null) {
            if (graduacaoExistente.getCoordenadorDoCurso() == null || !graduacaoDTO.getCoordenadorDoCursoId().equals(graduacaoExistente.getCoordenadorDoCurso().getId())) {
                Professor novoCoordenador = professorRepository.findById(graduacaoDTO.getCoordenadorDoCursoId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Professor (coordenador) com ID " + graduacaoDTO.getCoordenadorDoCursoId() + " não encontrado."));
                if (!novoCoordenador.getUniversidade().equals(universidade)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Novo professor (coordenador) não pertence à mesma universidade da graduação.");
                }
                graduacaoExistente.setCoordenadorDoCurso(novoCoordenador);
            }
        } else {
            graduacaoExistente.setCoordenadorDoCurso(null); // Remove coordenador if ID is null
        }

        return graduacaoRepository.save(graduacaoExistente);
    }

    @Transactional
    public void deletarGraduacao(Long graduacaoId) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Graduacao graduacao = buscarGraduacaoPorIdDaUniversidadeLogada(graduacaoId); // Validates ownership

        // Check for dependencies before deleting
        if (!graduacao.getAlunos().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não é possível excluir a graduação. Existem " + graduacao.getAlunos().size() + " aluno(s) associado(s).");
        }
        if (!graduacao.getMaterias().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não é possível excluir a graduação. Existem " + graduacao.getMaterias().size() + " matéria(s) associada(s).");
        }
        // Add more checks if Graduacao is linked to Turmas directly or other entities

        graduacaoRepository.delete(graduacao);
    }
} 