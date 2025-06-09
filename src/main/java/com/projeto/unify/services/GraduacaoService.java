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
import com.projeto.unify.repositories.UniversidadeRepository;
import com.projeto.unify.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GraduacaoService {

    // private static final Logger logger = LoggerFactory.getLogger(GraduacaoService.class);
    private final GraduacaoRepository graduacaoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioService usuarioService; // To get current user details
    private final ProfessorRepository professorRepository; // To assign coordenador
    private final UniversidadeRepository universidadeRepository; // Added if needed for fetching Universidade by ID

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

        Graduacao graduacao = new Graduacao();
        graduacao.setTitulo(dto.getTitulo());
        graduacao.setSemestres(dto.getSemestres());
        graduacao.setCodigoCurso(dto.getCodigoCurso());
        graduacao.setCampusDisponiveis(dto.getCampusDisponiveis());
        graduacao.setUniversidade(universidade);

        if (dto.getCoordenadorDoCursoId() != null) {
            Professor coordenador = professorRepository.findById(dto.getCoordenadorDoCursoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor (coordenador) não encontrado com o ID fornecido."));
            if (!coordenador.getUniversidade().equals(universidade)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coordenador selecionado não pertence à universidade do funcionário.");
            }
            graduacao.setCoordenadorDoCurso(coordenador);
        }

        Graduacao savedGraduacao = graduacaoRepository.save(graduacao);

        return savedGraduacao;
    }

    @Transactional(readOnly = true)
    public Graduacao buscarPorId(Long id) {
        Universidade universidadeDoFuncionario = getUniversidadeDoFuncionarioLogado();
        Graduacao graduacao = graduacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Graduação não encontrada"));

        if (!Objects.equals(graduacao.getUniversidade().getId(), universidadeDoFuncionario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado a esta graduação.");
        }
        
        graduacao.getCampusDisponiveis().size(); 

        return graduacao;
    }

    @Transactional(readOnly = true)
    public List<Graduacao> listarTodasPorUniversidadeDoUsuarioLogado() {
        Universidade universidadeDoFuncionario = getUniversidadeDoFuncionarioLogado();
        if (universidadeDoFuncionario == null) {
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não está associado a uma universidade.");
        }
        List<Graduacao> graduacoes = graduacaoRepository.findByUniversidade(universidadeDoFuncionario);
        // Eagerly initialize the campusDisponiveis collection for each Graduacao
        if (graduacoes != null) {
            for (Graduacao g : graduacoes) {
                g.getCampusDisponiveis().size(); // Accessing the collection to ensure it's loaded
            }
        }
        return graduacoes;
    }

    @Transactional
    public Graduacao atualizar(Long id, GraduacaoDTO dto) {
        Graduacao graduacaoExistente = buscarPorId(id); // Ensures it exists and user has access
        Universidade universidadeDoFuncionario = getUniversidadeDoFuncionarioLogado(); // For validation

        graduacaoExistente.setTitulo(dto.getTitulo());
        graduacaoExistente.setSemestres(dto.getSemestres());
        graduacaoExistente.setCodigoCurso(dto.getCodigoCurso());
        graduacaoExistente.setCampusDisponiveis(dto.getCampusDisponiveis());

        if (dto.getCoordenadorDoCursoId() != null) {
            Professor coordenador = professorRepository.findById(dto.getCoordenadorDoCursoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor (coordenador) não encontrado com o ID: " + dto.getCoordenadorDoCursoId()));
            
            // Validate that the chosen professor belongs to the same university
            if (!coordenador.getUniversidade().equals(universidadeDoFuncionario)) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coordenador selecionado não pertence à universidade do funcionário.");
            }
            graduacaoExistente.setCoordenadorDoCurso(coordenador);
        } else {
            graduacaoExistente.setCoordenadorDoCurso(null); // Allow unsetting the coordinator
        }

        return graduacaoRepository.save(graduacaoExistente);
    }

    @Transactional
    public void deletar(Long id) {
        Graduacao graduacao = buscarPorId(id); // Ensures it exists and user has access
        graduacaoRepository.delete(graduacao);
    }

    @Transactional(readOnly = true)
    public List<String> findCampusesByMateriaId(Long materiaId) {
        return graduacaoRepository.findCampusesByMateriaId(materiaId);
    }
} 