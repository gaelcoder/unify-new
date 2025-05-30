/* package com.projeto.unify.services;

import com.projeto.unify.dtos.ProfessorDTO; // Assuming ProfessorDTO exists or will be created
import com.projeto.unify.models.Professor;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.models.Universidade;
import com.projeto.unify.models.Representante;
import com.projeto.unify.models.Perfil;
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

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final RepresentanteRepository representanteRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService; // For finding logged-in user
    private final PasswordEncoder passwordEncoder; // For setting initial password
    private final PerfilService perfilService; // For assigning roles
    private final EmailService emailService; // For sending credentials

    @Transactional
    public Professor criar(ProfessorDTO dto) { 
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario adminUser = usuarioService.findByEmail(emailUsuarioLogado);
        Representante adminUniversidade = representanteRepository.findByUsuarioId(adminUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário logado não é um representante de universidade válido."));
        Universidade universidadeDoAdmin = adminUniversidade.getUniversidade();
        if (universidadeDoAdmin == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Representante não está associado a nenhuma universidade.");
        }

        // CPF Validation
        if (dto.getCpf() != null && !dto.getCpf().isBlank()) {
            if (professorRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado para um professor.");
            }
            if (alunoRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado para um aluno.");
            }
            if (funcionarioRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado para um funcionário.");
            }
            if (representanteRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado para um representante.");
            }
        }

        // Email (Personal/Login) Validation
        // Assuming dto.getEmail() is the primary email for the professor, potentially for login
        String emailProfessor = dto.getEmail(); 
        if (emailProfessor != null && !emailProfessor.isBlank()) {
            if (usuarioRepository.existsByEmail(emailProfessor)) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado para um usuário no sistema.");
            }
            if (professorRepository.existsByEmail(emailProfessor)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado para outro professor.");
            }
            if (alunoRepository.existsByEmail(emailProfessor)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado para um aluno.");
            }
            if (funcionarioRepository.existsByEmail(emailProfessor)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado para um funcionário.");
            }
            if (representanteRepository.existsByEmail(emailProfessor)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado para um representante.");
            }
        }

        // Telefone Validation
        if (dto.getTelefone() != null && !dto.getTelefone().isBlank()) {
            if (professorRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado para um professor.");
            }
            if (alunoRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado para um aluno.");
            }
            if (funcionarioRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado para um funcionário.");
            }
            if (representanteRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado para um representante.");
            }
        }
        
        // Criar Usuario para o Professor
        Usuario novoUsuarioProfessor = new Usuario();
        // TODO: Definir a lógica de email institucional para professor, se aplicável.
        // Por agora, usando o email pessoal do DTO como email do Usuario.
        novoUsuarioProfessor.setEmail(emailProfessor); 
        novoUsuarioProfessor.setNome(dto.getNome() + " " + dto.getSobrenome());
        novoUsuarioProfessor.setPrimeiroAcesso(true);
        String senhaTemporaria = gerarSenhaAleatoria();
        novoUsuarioProfessor.setSenha(passwordEncoder.encode(senhaTemporaria));
        Perfil perfilProfessor = perfilService.obterOuCriarPerfil(Perfil.TipoPerfil.ROLE_PROFESSOR);
        novoUsuarioProfessor.setPerfis(Set.of(perfilProfessor));
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuarioProfessor);

        Professor professor = new Professor();
        professor.setCpf(dto.getCpf());
        professor.setNome(dto.getNome());
        professor.setSobrenome(dto.getSobrenome());
        professor.setDataNascimento(dto.getDataNascimento());
        professor.setEmail(emailProfessor); 
        professor.setTelefone(dto.getTelefone());
        professor.setSalario(dto.getSalario());
        professor.setTitulacao(dto.getTitulacao());
        professor.setUniversidade(universidadeDoAdmin); 
        professor.setUsuario(usuarioSalvo);
        
        Professor professorSalvo = professorRepository.save(professor);

        try {
            if (emailService != null && emailProfessor != null && !emailProfessor.isBlank()) {
                emailService.enviarCredenciaisAcesso(emailProfessor, emailProfessor, senhaTemporaria, professor.getNomeCompleto());
            }
        } catch (Exception e) {
            System.err.println("Falha ao enviar email de primeiro acesso para " + emailProfessor + ": " + e.getMessage());
        }

        return professorSalvo;
    }

    @Transactional
    public Professor atualizar(Long id, ProfessorDTO dto) {
        Professor professorExistente = professorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado com ID: " + id));
        
        // TODO: Adicionar verificação de permissão (ex: se o usuário logado pode alterar este professor)

        // CPF Validation (if changed)
        if (dto.getCpf() != null && !dto.getCpf().isBlank() && !dto.getCpf().equals(professorExistente.getCpf())) {
            if (professorRepository.existsByCpf(dto.getCpf()) || // Check other professors
                alunoRepository.existsByCpf(dto.getCpf()) ||
                funcionarioRepository.existsByCpf(dto.getCpf()) ||
                representanteRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado no sistema.");
            }
            professorExistente.setCpf(dto.getCpf());
        }

        // Email Validation (if changed)
        String novoEmail = dto.getEmail();
        if (novoEmail != null && !novoEmail.isBlank() && !novoEmail.equals(professorExistente.getEmail())) {
            // Check across all user emails and other entities' emails
            if (usuarioRepository.existsByEmail(novoEmail) ||
                alunoRepository.existsByEmail(novoEmail) ||
                funcionarioRepository.existsByEmail(novoEmail) ||
                representanteRepository.existsByEmail(novoEmail) ||
                professorRepository.findByEmail(novoEmail).filter(p -> !p.getId().equals(id)).isPresent()) { // Check other professors
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado no sistema.");
            }
            professorExistente.setEmail(novoEmail);
            // Update Usuario email as well
            Usuario usuarioDoProfessor = professorExistente.getUsuario();
            if (usuarioDoProfessor != null) {
                usuarioDoProfessor.setEmail(novoEmail);
                usuarioRepository.save(usuarioDoProfessor);
            }
        }

        // Telefone Validation (if changed)
        if (dto.getTelefone() != null && !dto.getTelefone().isBlank() && !dto.getTelefone().equals(professorExistente.getTelefone())) {
            if (alunoRepository.existsByTelefone(dto.getTelefone()) ||
                funcionarioRepository.existsByTelefone(dto.getTelefone()) ||
                representanteRepository.existsByTelefone(dto.getTelefone()) ||
                professorRepository.findByTelefone(dto.getTelefone()).filter(p -> !p.getId().equals(id)).isPresent()) { // Check other professors
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado no sistema.");
            }
            professorExistente.setTelefone(dto.getTelefone());
        }

        if (dto.getNome() != null) professorExistente.setNome(dto.getNome());
        if (dto.getSobrenome() != null) professorExistente.setSobrenome(dto.getSobrenome());
        if (dto.getDataNascimento() != null) professorExistente.setDataNascimento(dto.getDataNascimento());
        if (dto.getSalario() != null) professorExistente.setSalario(dto.getSalario()); // Assuming ProfessorDTO has getSalario
        if (dto.getTitulacao() != null) professorExistente.setTitulacao(dto.getTitulacao()); // Assuming ProfessorDTO has getTitulacao
        // Universidade and Usuario are not typically changed this way.

        return professorRepository.save(professorExistente);
    }
    
    @Transactional(readOnly = true)
    public List<Professor> listarTodos() {
        // TODO: Add security/permission checks if needed, e.g., list only for user's university
        return professorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Professor buscarPorId(Long id) {
        // TODO: Add security/permission checks
        return professorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado com ID: " + id));
    }

    @Transactional
    public void deletar(Long id) {
        // TODO: Add security/permission checks
        Professor professor = buscarPorId(id);
        Usuario usuarioAssociado = professor.getUsuario();
        professorRepository.delete(professor);
        if (usuarioAssociado != null) {
            // Consider if user should be deleted or just unlinked/deactivated
            usuarioRepository.delete(usuarioAssociado);
        }
    }

    private String gerarSenhaAleatoria() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    // Add other methods like buscarPorId, listarTodos, atualizar, deletar as needed
}*/