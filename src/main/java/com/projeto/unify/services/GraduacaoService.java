package com.projeto.unify.services;

import com.projeto.unify.dtos.GraduacaoDTO;
import com.projeto.unify.models.Funcionario;
import com.projeto.unify.models.Graduacao;
import com.projeto.unify.models.Professor;
import com.projeto.unify.models.Universidade;
import com.projeto.unify.models.Usuario;
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
    private final UsuarioService usuarioService; // To get current user details
    private final ProfessorRepository professorRepository; // To assign coordenador

    private Funcionario getFuncionarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);
        return funcionarioRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não encontrado para o usuário logado."));
    }

    private Universidade getUniversidadeDoFuncionarioLogado() {
        Funcionario funcionario = getFuncionarioLogado();
        if (funcionario.getUniversidade() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Funcionário logado não está associado a nenhuma universidade.");
        }
        return funcionario.getUniversidade();
    }

    @Transactional
    public Graduacao criar(GraduacaoDTO dto) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();

        // TODO: Add validation to ensure codigoCurso is unique within the university

        Graduacao graduacao = new Graduacao();
        graduacao.setTitulo(dto.getTitulo());
        graduacao.setSemestres(dto.getSemestres());
        graduacao.setCodigoCurso(dto.getCodigoCurso());
        graduacao.setCampusDisponivel(dto.getCampusDisponivel());
        graduacao.setUniversidade(universidade);

        if (dto.getCoordenadorDoCursoId() != null) {
            Professor coordenador = professorRepository.findById(dto.getCoordenadorDoCursoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor (coordenador) não encontrado com o ID fornecido."));
            if (!coordenador.getUniversidade().equals(universidade)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coordenador selecionado não pertence à universidade do funcionário.");
            }
            graduacao.setCoordenadorDoCurso(coordenador);
        }

        return graduacaoRepository.save(graduacao);
    }

    public List<Graduacao> listarTodasPorUniversidadeDoUsuarioLogado() {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        return graduacaoRepository.findByUniversidade(universidade);
    }

    public Graduacao buscarPorIdEUniversidadeDoUsuarioLogado(Long id) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        return graduacaoRepository.findByIdAndUniversidade(id, universidade)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Graduação não encontrada ou não pertence à sua universidade."));
    }

    @Transactional
    public Graduacao atualizar(Long id, GraduacaoDTO dto) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Graduacao graduacao = graduacaoRepository.findByIdAndUniversidade(id, universidade)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Graduação não encontrada ou não pertence à sua universidade para atualização."));

        // TODO: Add validation to ensure codigoCurso is unique within the university (if changed)

        graduacao.setTitulo(dto.getTitulo());
        graduacao.setSemestres(dto.getSemestres());
        graduacao.setCodigoCurso(dto.getCodigoCurso());
        graduacao.setCampusDisponivel(dto.getCampusDisponivel());

        if (dto.getCoordenadorDoCursoId() != null) {
            Professor coordenador = professorRepository.findById(dto.getCoordenadorDoCursoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor (coordenador) não encontrado com o ID fornecido."));
            if (!coordenador.getUniversidade().equals(universidade)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coordenador selecionado não pertence à universidade do funcionário.");
            }
            graduacao.setCoordenadorDoCurso(coordenador);
        } else {
            graduacao.setCoordenadorDoCurso(null); // Allows removing the coordinator
        }

        return graduacaoRepository.save(graduacao);
    }

    @Transactional
    public void deletar(Long id) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Graduacao graduacao = graduacaoRepository.findByIdAndUniversidade(id, universidade)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Graduação não encontrada ou não pertence à sua universidade para deleção."));
        
        // TODO: Add business logic: check if there are Alunos associated before deleting? Or Materias?
        // For now, direct deletion.

        graduacaoRepository.delete(graduacao);
    }
} 