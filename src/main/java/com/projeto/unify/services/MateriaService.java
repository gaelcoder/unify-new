package com.projeto.unify.services;

import com.projeto.unify.dtos.MateriaDTO;
import com.projeto.unify.models.Funcionario;
import com.projeto.unify.models.Graduacao;
import com.projeto.unify.models.Materia;
import com.projeto.unify.models.Universidade;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.repositories.FuncionarioRepository;
import com.projeto.unify.repositories.GraduacaoRepository;
import com.projeto.unify.repositories.MateriaRepository;
import com.projeto.unify.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository materiaRepository;
    private final GraduacaoRepository graduacaoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioService usuarioService;

    private Universidade getUniversidadeDoFuncionarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);

        Funcionario funcionario = funcionarioRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário logado não é um funcionário válido ou não encontrado."));

        if (funcionario.getUniversidade() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Funcionário logado não está associado a nenhuma universidade.");
        }
        return funcionario.getUniversidade();
    }

    @Transactional
    public Materia criarMateria(MateriaDTO materiaDTO) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();

        if (materiaRepository.existsByCodigoAndUniversidade(materiaDTO.getCodigo(), universidade)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe uma matéria com o código " + materiaDTO.getCodigo() + " nesta universidade.");
        }

        Materia materia = new Materia();
        materia.setTitulo(materiaDTO.getTitulo());
        materia.setCodigo(materiaDTO.getCodigo());
        materia.setCreditos(materiaDTO.getCreditos());
        materia.setCargaHoraria(materiaDTO.getCargaHoraria());
        materia.setCreditosNecessarios(materiaDTO.getCreditosNecessarios() != null ? materiaDTO.getCreditosNecessarios() : 0);
        materia.setUniversidade(universidade);

        Set<Graduacao> graduacoes = new HashSet<>();
        if (materiaDTO.getGraduacaoIds() != null && !materiaDTO.getGraduacaoIds().isEmpty()) {
            for (Long graduacaoId : materiaDTO.getGraduacaoIds()) {
                Graduacao grad = graduacaoRepository.findByIdAndUniversidade(graduacaoId, universidade)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Graduação com ID " + graduacaoId + " não encontrada ou não pertence à sua universidade."));
                graduacoes.add(grad);
            }
        }
        materia.setGraduacoes(graduacoes);

        return materiaRepository.save(materia);
    }

    @Transactional
    public Materia atualizarMateria(Long materiaId, MateriaDTO materiaDTO) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Materia materia = materiaRepository.findByIdAndUniversidade(materiaId, universidade)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matéria não encontrada ou não pertence à sua universidade."));

        // Check if codigo is being changed and if it conflicts
        if (!materia.getCodigo().equals(materiaDTO.getCodigo())) {
            if (materiaRepository.existsByCodigoAndUniversidade(materiaDTO.getCodigo(), universidade)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe outra matéria com o código " + materiaDTO.getCodigo() + " nesta universidade.");
            }
            materia.setCodigo(materiaDTO.getCodigo());
        }

        materia.setTitulo(materiaDTO.getTitulo());
        materia.setCreditos(materiaDTO.getCreditos());
        materia.setCargaHoraria(materiaDTO.getCargaHoraria());
        materia.setCreditosNecessarios(materiaDTO.getCreditosNecessarios() != null ? materiaDTO.getCreditosNecessarios() : 0);
        // Universidade of Materia does not change

        Set<Graduacao> graduacoes = new HashSet<>();
        if (materiaDTO.getGraduacaoIds() != null && !materiaDTO.getGraduacaoIds().isEmpty()) {
            for (Long graduacaoId : materiaDTO.getGraduacaoIds()) {
                Graduacao grad = graduacaoRepository.findByIdAndUniversidade(graduacaoId, universidade)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Graduação com ID " + graduacaoId + " não encontrada ou não pertence à sua universidade para associação."));
                graduacoes.add(grad);
            }
        }
        materia.getGraduacoes().clear();
        materia.getGraduacoes().addAll(graduacoes);

        return materiaRepository.save(materia);
    }

    public List<Materia> listarMateriasPorUniversidadeDoFuncionarioLogado() {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        List<Materia> materias = materiaRepository.findByUniversidade(universidade);
        return materias;
    }

    public Materia buscarMateriaPorIdEUniversidade(Long materiaId) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        return materiaRepository.findByIdAndUniversidade(materiaId, universidade)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matéria não encontrada ou não pertence à sua universidade."));
    }

    @Transactional
    public void deletarMateria(Long materiaId) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Materia materia = materiaRepository.findByIdAndUniversidade(materiaId, universidade)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matéria não encontrada para exclusão ou não pertence à sua universidade."));
        if (!materia.getTurmas().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matéria não pode ser excluída pois está associada a uma ou mais turmas.");
        }

        materiaRepository.delete(materia);
    }
} 