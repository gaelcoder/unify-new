package com.projeto.unify.services;

import com.projeto.unify.dtos.AlunoDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.repositories.*;
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
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ProfessorRepository professorRepository;
    private final RepresentanteRepository representanteRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService; 
    private final PasswordEncoder passwordEncoder;
    private final PerfilService perfilService;
    private final EmailService emailService;
    private final UniversidadeRepository universidadeRepository; // Assuming Aluno is tied to a universidade
    private final TurmaRepository turmaRepository; // Assuming Aluno can be tied to a turma
    private final GraduacaoRepository graduacaoRepository; // Assuming Aluno can be tied to a graduacao

    @Transactional
    public Aluno criar(AlunoDTO dto) { 
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario adminUser = usuarioService.findByEmail(emailUsuarioLogado);
        
        // Determine a qual universidade o aluno sera vinculado
        // Isso pode vir do DTO (dto.getUniversidadeId()) ou do usuario logado (se for um admin de universidade)
        Universidade universidadeDoAluno;
        if (dto.getUniversidadeId() != null) {
            universidadeDoAluno = universidadeRepository.findById(dto.getUniversidadeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Universidade não encontrada."));
        } else {
             // Fallback para a universidade do admin que está criando, se aplicável
             Representante adminUniversidade = representanteRepository.findByUsuarioId(adminUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário logado não é um representante de universidade válido ou ID da universidade não fornecido."));
            universidadeDoAluno = adminUniversidade.getUniversidade();
            if (universidadeDoAluno == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Representante não está associado a nenhuma universidade.");
            }
        }

        // CPF Validation
        if (dto.getCpf() != null && !dto.getCpf().isBlank()) {
            if (alunoRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado para um aluno.");
            }
            if (funcionarioRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado para um funcionário.");
            }
            if (professorRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado para um professor.");
            }
            if (representanteRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado para um representante.");
            }
        }

        // Email (Personal/Login) Validation
        String emailAluno = dto.getEmail(); 
        if (emailAluno != null && !emailAluno.isBlank()) {
            if (usuarioRepository.existsByEmail(emailAluno)) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado para um usuário no sistema.");
            }
            if (alunoRepository.existsByEmail(emailAluno)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado para outro aluno.");
            }
            if (funcionarioRepository.existsByEmail(emailAluno)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado para um funcionário.");
            }
            if (professorRepository.existsByEmail(emailAluno)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado para um professor.");
            }
            if (representanteRepository.existsByEmail(emailAluno)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado para um representante.");
            }
        }

        // Telefone Validation
        if (dto.getTelefone() != null && !dto.getTelefone().isBlank()) {
            if (alunoRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado para um aluno.");
            }
            if (funcionarioRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado para um funcionário.");
            }
            if (professorRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado para um professor.");
            }
            if (representanteRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado para um representante.");
            }
        }
        
        Usuario novoUsuarioAluno = new Usuario();
        // TODO: Definir a lógica de email institucional para aluno
        novoUsuarioAluno.setEmail(emailAluno); 
        novoUsuarioAluno.setNome(dto.getNome() + " " + dto.getSobrenome());
        novoUsuarioAluno.setPrimeiroAcesso(true);
        String senhaTemporaria = gerarSenhaAleatoria();
        novoUsuarioAluno.setSenha(passwordEncoder.encode(senhaTemporaria));
        Perfil perfilAluno = perfilService.obterOuCriarPerfil(Perfil.TipoPerfil.ROLE_ALUNO);
        novoUsuarioAluno.setPerfis(Set.of(perfilAluno));
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuarioAluno);

        Aluno aluno = new Aluno();
        aluno.setCpf(dto.getCpf());
        aluno.setNome(dto.getNome());
        aluno.setSobrenome(dto.getSobrenome());
        aluno.setDataNascimento(dto.getDataNascimento());
        aluno.setEmail(emailAluno);
        aluno.setTelefone(dto.getTelefone());
        aluno.setMatricula(dto.getMatricula()); 
        aluno.setCurso(dto.getCurso()); 
        aluno.setCr(dto.getCr());
        aluno.setUniversidade(universidadeDoAluno);
        aluno.setUsuario(usuarioSalvo);

        if (dto.getTurmaId() != null) {
            Turma turma = turmaRepository.findById(dto.getTurmaId()).orElse(null); // Nao lanca excecao se turma nao existir, apenas nao associa
            aluno.setTurma(turma);
        }
        if (dto.getGraduacaoId() != null) {
            Graduacao graduacao = graduacaoRepository.findById(dto.getGraduacaoId()).orElse(null);
            aluno.setGraduacao(graduacao);
        }
        
        Aluno alunoSalvo = alunoRepository.save(aluno);

        try {
            if (emailService != null && emailAluno != null && !emailAluno.isBlank()) {
                emailService.enviarCredenciaisAcesso(emailAluno, emailAluno, senhaTemporaria, aluno.getNomeCompleto());
            }
        } catch (Exception e) {
            System.err.println("Falha ao enviar email de primeiro acesso para " + emailAluno + ": " + e.getMessage());
        }
        return alunoSalvo;
    }

    private String gerarSenhaAleatoria() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
*/
