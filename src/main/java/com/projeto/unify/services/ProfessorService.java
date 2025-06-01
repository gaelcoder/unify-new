package com.projeto.unify.services;

import com.projeto.unify.dtos.FuncionarioDTO;
import com.projeto.unify.dtos.ProfessorDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.repositories.AlunoRepository;
import com.projeto.unify.repositories.FuncionarioRepository;
import com.projeto.unify.repositories.ProfessorRepository;
import com.projeto.unify.repositories.RepresentanteRepository;
import com.projeto.unify.repositories.UsuarioRepository;
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

    private static final String TITULO_DR = "Dr. / Dr.a";
    private static final String TITULO_MESTRE = "M.e / M.ª";
    private static final String TITULO_ESPECIALISTA = "Especialista";
    private static final List<String> TITULACOES_VALIDAS = Arrays.asList(TITULO_DR, TITULO_ESPECIALISTA, TITULO_MESTRE);


    private final FuncionarioRepository funcionarioRepository;
    private final RepresentanteRepository representanteRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PerfilService perfilService;
    private final EmailService emailService;

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

    private Universidade getUniversidadeDoUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);

        Professor professor = professorRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Usuário não é um usuário de RH válido para acessar esta funcionalidade."));

        Universidade universidade = professor.getUniversidade();
        if (universidade == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Professor não está associado a nenhuma universidade.");
        }
        return universidade;
    }

    @Transactional(readOnly = true)
    public List<Professor> listarTodosPorUniversidadeDoUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isRHUniversidade = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FUNCIONARIO_RH"));

        if (isRHUniversidade) {
          return professorRepository.findAll();
        }

        Universidade universidade = getUniversidadeDoUsuarioLogado();
        return professorRepository.findByUniversidadeId(universidade.getId());
    }

}