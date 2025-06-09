package com.projeto.unify.services;

import com.projeto.unify.dtos.ProfessorDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.repositories.AlunoRepository;
import com.projeto.unify.repositories.FuncionarioRepository;
import com.projeto.unify.repositories.ProfessorRepository;
import com.projeto.unify.repositories.RepresentanteRepository;
import com.projeto.unify.repositories.UsuarioRepository;
import com.projeto.unify.repositories.GraduacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    // Updated list of valid titulations to match frontend preference
    private static final List<String> TITULACOES_VALIDAS = Arrays.asList(
        "Graduado", 
        "Especialista", 
        "Mestre", 
        "Doutor", 
        "Pós-Doutor"
    );

    private final FuncionarioRepository funcionarioRepository;
    private final RepresentanteRepository representanteRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PerfilService perfilService;
    private final EmailService emailService;
    private final GraduacaoRepository graduacaoRepository;

    @Transactional
    public Professor criar(ProfessorDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();

        Usuario rhUser = usuarioService.findByEmail(emailUsuarioLogado);

        Funcionario rhUniversidade = funcionarioRepository.findByUsuarioId(rhUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Usuário logado não é do setor de RH."));

        Universidade universidadeDoRH = rhUniversidade.getUniversidade();
        if (universidadeDoRH == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Funcionário do RH não está associado a nenhuma universidade.");
        }

        if (dto.getTitulacao() == null || dto.getTitulacao().isBlank() || !TITULACOES_VALIDAS.contains(dto.getTitulacao())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Titulação é obrigatória.");
        }

        String emailInstitucional = gerarEmailInstitucionalProfessor(dto, universidadeDoRH, dto.getTitulacao());

        if (usuarioRepository.existsByEmail(emailInstitucional)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email institucional gerado já cadastrado no sistema.");
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equals(emailInstitucional) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email informado já cadastrado no sistema.");
        }

        // Validacao cruzada de Email Pessoal
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (alunoRepository.existsByEmail(dto.getEmail()) ||
                    professorRepository.existsByEmail(dto.getEmail()) ||
                    representanteRepository.existsByEmail(dto.getEmail())
            ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email pessoal já cadastrado para um aluno.");
            }
        }

        if (dto.getTelefone() != null && !dto.getTelefone().isBlank()) {
            if (funcionarioRepository.existsByTelefone(dto.getTelefone()) ||
                    alunoRepository.existsByTelefone(dto.getTelefone()) ||
                    professorRepository.existsByTelefone(dto.getTelefone()) ||
                    representanteRepository.existsByTelefone(dto.getTelefone())
            ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado no sistema.");
            }
        }

        if (dto.getCpf() != null) {
            if (funcionarioRepository.existsByCpf(dto.getCpf()) ||
                    representanteRepository.existsByCpf(dto.getCpf()) ||
                    alunoRepository.existsByCpf(dto.getCpf()) ||
                    professorRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado no sistema.");
            }
        }

        Usuario novoUsuarioProfessor= new Usuario();
        novoUsuarioProfessor.setEmail(emailInstitucional);
        novoUsuarioProfessor.setNome(dto.getNome() + " " + dto.getSobrenome());
        novoUsuarioProfessor.setPrimeiroAcesso(true);

        String senhaTemporaria = gerarSenhaAleatoria();
        novoUsuarioProfessor.setSenha(passwordEncoder.encode(senhaTemporaria));

        Perfil perfilProfessor;
        perfilProfessor = perfilService.obterOuCriarPerfil(Perfil.TipoPerfil.ROLE_PROFESSOR);
        
        novoUsuarioProfessor.setPerfis(Set.of(perfilProfessor));

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuarioProfessor);

        Professor professor = new Professor();
        professor.setCpf(dto.getCpf());
        professor.setDataNascimento(dto.getDataNascimento());
        professor.setNome(dto.getNome());
        professor.setSobrenome(dto.getSobrenome());
        professor.setEmail(dto.getEmail());
        professor.setTelefone(dto.getTelefone());
        professor.setTitulacao(dto.getTitulacao());
        professor.setSalario(dto.getSalario());
        professor.setUniversidade(universidadeDoRH);
        professor.setUsuario(usuarioSalvo);

        Professor professorSalvo = professorRepository.save(professor);

        try {
            String emailDestinatario = (dto.getEmail() != null && !dto.getEmail().isBlank()) ? dto.getEmail() : emailInstitucional;
            if (emailService != null) {
                String nomeDestinatario = professor.getNomeCompleto();
                emailService.enviarCredenciaisAcesso(emailDestinatario, emailInstitucional, senhaTemporaria, nomeDestinatario);
            }
        } catch (Exception e) {
            System.err.println("Falha ao enviar email de primeiro acesso para " + usuarioSalvo.getEmail() + ": " + e.getMessage());
        }

        return professorSalvo;
    }

    public List<Professor> listarTodos() {
        return professorRepository.findAll();
    }

    private String gerarEmailInstitucionalProfessor(ProfessorDTO dto, Universidade universidade, String setor) {
        String primeiroNome = dto.getNome().toLowerCase()
                .split(" ")[0]
                .replace("ç", "c")
                .replace("á", "a")
                .replace("à", "a")
                .replace("ã", "a")
                .replace("â", "a")
                .replace("é", "e")
                .replace("ê", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ô", "o")
                .replace("õ", "o")
                .replace("ú", "u")
                .replace("ü", "u");

        String ultimoSobrenome = dto.getSobrenome().toLowerCase();
        if (ultimoSobrenome.contains(" ")) {
            String[] partes = ultimoSobrenome.split(" ");
            ultimoSobrenome = partes[partes.length - 1];
        }
        ultimoSobrenome = ultimoSobrenome
                .replace("ç", "c")
                .replace("á", "a")
                .replace("à", "a")
                .replace("ã", "a")
                .replace("â", "a")
                .replace("é", "e")
                .replace("ê", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ô", "o")
                .replace("õ", "o")
                .replace("ú", "u")
                .replace("ü", "u");

        String nomeUniversidade = universidade.getNome().toLowerCase()
                .replace(" ", "")
                .replace("universidade", "")
                .replace("faculdade", "")
                .replace("instituto", "")
                .replace("ç", "c")
                .replace("á", "a")
                .replace("à", "a")
                .replace("ã", "a")
                .replace("â", "a")
                .replace("é", "e")
                .replace("ê", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ô", "o")
                .replace("õ", "o")
                .replace("ú", "u")
                .replace("ü", "u");

        String parteUniversidade;
        if (nomeUniversidade.length() > 10 && universidade.getSigla() != null && !universidade.getSigla().isEmpty()) {
            parteUniversidade = universidade.getSigla().toLowerCase();
        } else {
            parteUniversidade = nomeUniversidade;
        }

        String infixEmail = "prof";
        return primeiroNome + "." + ultimoSobrenome + "@" + infixEmail + "." + parteUniversidade + ".unify.edu.com";
    }

    private String gerarSenhaAleatoria() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    // Helper method to get the University of the currently logged-in RH Funcionario
    private Universidade getUniversidadeDoFuncionarioRHLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);

        // Assuming FUNCIONARIO_RH is a type of Funcionario
        Funcionario funcionario = funcionarioRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Usuário logado não é um funcionário válido para esta operação."));

        Universidade universidade = funcionario.getUniversidade();
        if (universidade == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Funcionário não está associado a nenhuma universidade.");
        }
        return universidade;
    }

    @Transactional(readOnly = true)
    public List<Professor> listarTodosPorUniversidadeDoUsuarioLogado() {
        Universidade universidadeDoRH = getUniversidadeDoFuncionarioRHLogado();
        return professorRepository.findByUniversidadeId(universidadeDoRH.getId());
    }

    @Transactional(readOnly = true)
    public Professor buscarPorIdEUniversidadeDoUsuarioLogado(Long id) {
        Universidade universidade = getUniversidadeDoFuncionarioRHLogado();
        return professorRepository.findByIdAndUniversidade(id, universidade)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado ou não pertence à sua universidade."));
    }

    public Professor buscarProfessorPorId(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado com o ID: " + id));
    }

    @Transactional
    public Professor atualizarProfessor(Long id, ProfessorDTO dto) {
        Professor professor = buscarProfessorPorId(id); // Reuses the findById logic

        // Basic field updates
        professor.setNome(dto.getNome());
        professor.setSobrenome(dto.getSobrenome());
        professor.setDataNascimento(dto.getDataNascimento());
        professor.setTelefone(dto.getTelefone());
        professor.setSalario(dto.getSalario());

        if (dto.getTitulacao() == null || dto.getTitulacao().isBlank() || !TITULACOES_VALIDAS.contains(dto.getTitulacao())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Titulação é obrigatória e deve ser válida.");
        }
        professor.setTitulacao(dto.getTitulacao());

        // Email Pessoal - update if provided and different, check for conflicts
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equals(professor.getEmail())) {
            if (usuarioRepository.existsByEmailAndIdNot(dto.getEmail(), professor.getUsuario().getId()) || // Check other users
                alunoRepository.existsByEmailAndIdNot(dto.getEmail(), null) || // Assuming Aluno ID is not directly on Professor for this check
                funcionarioRepository.existsByEmailAndIdNot(dto.getEmail(), null) ||
                representanteRepository.existsByEmailAndIdNot(dto.getEmail(), null) ||
                professorRepository.existsByEmailAndIdNot(dto.getEmail(), professor.getId())) { // Check other professors
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email pessoal informado já cadastrado no sistema para outro usuário.");
            }
            professor.setEmail(dto.getEmail());
        }
        
        // CPF - update if provided and different, check for conflicts
        // Note: CPF is highly sensitive and usually not updatable or requires special permissions.
        // For now, allowing update but with conflict checks.
        if (dto.getCpf() != null && !dto.getCpf().isBlank() && !dto.getCpf().equals(professor.getCpf())) {
             if (alunoRepository.existsByCpfAndIdNot(dto.getCpf(), null) ||
                 funcionarioRepository.existsByCpfAndIdNot(dto.getCpf(), null) ||
                 representanteRepository.existsByCpfAndIdNot(dto.getCpf(), null) ||
                 professorRepository.existsByCpfAndIdNot(dto.getCpf(), professor.getId())) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF informado já cadastrado no sistema para outro usuário.");
             }
            professor.setCpf(dto.getCpf());
        }

        // Update associated Usuario name if professor name changes
        Usuario usuarioAssociado = professor.getUsuario();
        if (usuarioAssociado != null) {
            usuarioAssociado.setNome(professor.getNomeCompleto());
            usuarioRepository.save(usuarioAssociado);
        }

        return professorRepository.save(professor);
    }

    @Transactional
    public void deletarProfessor(Long id) {
        Professor professor = buscarProfessorPorId(id); // Ensures professor exists
        
        // Consider implications:
        // 1. What if the professor is a coordinator of a course? Prevent deletion or handle reassignment.
        //    For now, this is not handled here but should be a business rule.
        // 2. Associated Usuario: Deactivate or delete?
        //    For now, just deleting the professor record. The Usuario might remain or be handled by a separate process.

        professorRepository.delete(professor);
        
        // Optionally, delete or deactivate the associated Usuario
        // Usuario usuarioParaDeletar = professor.getUsuario();
        // if (usuarioParaDeletar != null) {
        //     usuarioRepository.delete(usuarioParaDeletar); // or set to inactive
        // }
    }

    // Method for Graduation Form - listing professors available for coordination
    @Transactional(readOnly = true)
    public List<Professor> listarProfessoresPorUniversidadeDisponiveisParaCoordenacao(Long universidadeId) {
        List<Professor> professoresDaUniversidade = professorRepository.findByUniversidadeId(universidadeId);
        List<Long> idsCoordenadores = graduacaoRepository.findDistinctCoordenadorDoCursoIdByUniversidadeId(universidadeId);

        if (idsCoordenadores == null || idsCoordenadores.isEmpty()) {
            return professoresDaUniversidade; // No coordinators found, return all professors from the university
        }

        // Filter out professors who are already coordinators
        // A professor is identified by their main ID (Professor.id), and Graduacao.coordenadorDoCursoId stores this Professor.id
        return professoresDaUniversidade.stream()
                .filter(professor -> !idsCoordenadores.contains(professor.getId()))
                .collect(java.util.stream.Collectors.toList());
    }

}