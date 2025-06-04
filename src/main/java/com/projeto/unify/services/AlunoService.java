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

import java.util.Set;
import java.util.UUID;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

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
    private final UniversidadeRepository universidadeRepository;
    private final GraduacaoRepository graduacaoRepository;

    @Transactional
    public Aluno criar(AlunoDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario logadoUser = usuarioService.findByEmail(emailUsuarioLogado);

        Universidade universidadeDoAluno = null;

        boolean isFuncionario = logadoUser.getPerfis().stream()
                .anyMatch(perfil -> perfil.getNome() == Perfil.TipoPerfil.ROLE_FUNCIONARIO);

        if (isFuncionario) {
            Funcionario funcionario = funcionarioRepository.findByUsuarioId(logadoUser.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não encontrado para o usuário logado."));
            if (funcionario.getUniversidade() != null) {
                universidadeDoAluno = funcionario.getUniversidade();
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Funcionário não está associado a nenhuma universidade.");
            }
        }

        // Se nenhuma universidade foi determinada
        if (universidadeDoAluno == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não foi possível determinar a universidade para o aluno. Verifique se o usuário logado (funcionário/admin universidade) está corretamente associado a uma universidade ou forneça uma ID de universidade no DTO.");
        }

        // Graduacao Validation and Fetching
        if (dto.getGraduacaoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID da Graduação é obrigatório.");
        }
        Graduacao graduacao = graduacaoRepository.findById(dto.getGraduacaoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Graduação não encontrada com o ID fornecido."));

        // Ensure the Graduacao belongs to the Aluno's Universidade
        if (!graduacao.getUniversidade().equals(universidadeDoAluno)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A Graduação especificada não pertence à universidade do aluno.");
        }

        // CPF Validation
        if (dto.getCpf() != null && !dto.getCpf().isBlank()) {
            if (alunoRepository.existsByCpf(dto.getCpf()) ||
                    funcionarioRepository.existsByCpf(dto.getCpf()) ||
                    professorRepository.existsByCpf(dto.getCpf()) ||
                    representanteRepository.existsByCpf(dto.getCpf())
            ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado.");
            }
        }

        // Email (Personal/Login) Validation
        String emailAluno = dto.getEmail(); 
        if (emailAluno != null && !emailAluno.isBlank()) {
            if (usuarioRepository.existsByEmail(emailAluno) ||
                    representanteRepository.existsByEmail(emailAluno) ||
                    funcionarioRepository.existsByEmail(emailAluno) ||
                    professorRepository.existsByEmail(emailAluno)

            ) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado no sistema.");
            }
        }

        // Telefone Validation
        if (dto.getTelefone() != null && !dto.getTelefone().isBlank()) {
            if (alunoRepository.existsByTelefone(dto.getTelefone()) ||
                    funcionarioRepository.existsByTelefone(dto.getTelefone()) ||
                    professorRepository.existsByTelefone(dto.getTelefone()) ||
                    representanteRepository.existsByTelefone(dto.getTelefone())
            ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado.");
            }
        }
        
        Usuario novoUsuarioAluno = new Usuario();
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
        String matriculaGerada = gerarMatricula(universidadeDoAluno);
        aluno.setMatricula(matriculaGerada);
        aluno.setUniversidade(universidadeDoAluno);
        aluno.setUsuario(usuarioSalvo);
        aluno.setGraduacao(graduacao);

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

    private String gerarMatricula(Universidade universidade) {
        LocalDate hoje = LocalDate.now();
        int ano = hoje.getYear();
        int mes = hoje.getMonthValue();

        String anoStr = String.valueOf(ano).substring(2);
        String semestreStr;

        if (mes >= Month.JANUARY.getValue() && mes <= Month.JUNE.getValue()) {
            semestreStr = "10"; // Primeiro semestre
        } else {
            semestreStr = "20"; // Segundo semestre
        }

        String prefixoMatricula = anoStr + semestreStr;

        long countAlunosComPrefixo = alunoRepository.countByUniversidadeAndMatriculaStartingWith(universidade, prefixoMatricula);
        long numeroSequencial = countAlunosComPrefixo + 1;

        // Formato: YYSS + numeroSequencial + "000"
        // Example: 24101000 (Ano 24, Semestre 10, Seq 1, sufixo 000)
        return prefixoMatricula + numeroSequencial + "000";
    }

    public List<Aluno> listarPorUniversidadeDoFuncionarioLogado() {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        return alunoRepository.findByUniversidade(universidade);
    }

    public Aluno buscarPorIdEUniversidadeDoFuncionarioLogado(Long alunoId) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        return alunoRepository.findByIdAndUniversidade(alunoId, universidade)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado ou não pertence à sua universidade."));
    }

    @Transactional
    public Aluno atualizar(Long alunoId, AlunoDTO dto) {
        Universidade uniFuncionario = getUniversidadeDoFuncionarioLogado();
        Aluno aluno = alunoRepository.findByIdAndUniversidade(alunoId, uniFuncionario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado ou não pertence à sua universidade para atualização."));

        // Basic field updates
        aluno.setNome(dto.getNome());
        aluno.setSobrenome(dto.getSobrenome());
        aluno.setDataNascimento(dto.getDataNascimento());
        aluno.setTelefone(dto.getTelefone());

        // Email update - needs careful handling due to Usuario link
        if (!aluno.getEmail().equals(dto.getEmail())) {
            // Check for email uniqueness across relevant entities (Usuario, other Pessoa types)
            if (usuarioRepository.existsByEmail(dto.getEmail()) && !aluno.getUsuario().getEmail().equals(dto.getEmail())) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Novo email já cadastrado para um usuário no sistema.");
            }
            // Potentially other checks from initial creation logic if email is widely unique
            aluno.setEmail(dto.getEmail());
            // Also update the associated Usuario's email if it's meant to be kept in sync
            Usuario usuarioDoAluno = aluno.getUsuario();
            if (usuarioDoAluno != null) {
                usuarioDoAluno.setEmail(dto.getEmail());
                // usuarioDoAluno.setNome(dto.getNome() + " " + dto.getSobrenome()); // Also update name if it changed
                usuarioRepository.save(usuarioDoAluno);
            }
        }
        // CPF is usually not updatable. Matricula is generated and fixed.
        // Universidade is fixed post-creation for an Aluno.

        // Graduacao update
        if (dto.getGraduacaoId() != null && (aluno.getGraduacao() == null || !aluno.getGraduacao().getId().equals(dto.getGraduacaoId()))) {
            Graduacao novaGraduacao = graduacaoRepository.findById(dto.getGraduacaoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nova graduação com ID " + dto.getGraduacaoId() + " não encontrada."));
            if (!novaGraduacao.getUniversidade().equals(uniFuncionario)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova graduação especificada não pertence à sua universidade.");
            }
            // TODO: Consider implications of changing graduacao (CR, turmas matriculadas, etc.)
            // For now, direct update. Matricula is NOT re-generated.
            aluno.setGraduacao(novaGraduacao);
        }
        // CR is calculated, not set directly here. Turma association is separate.

        return alunoRepository.save(aluno);
    }

    @Transactional
    public void deletar(Long alunoId) {
        Aluno aluno = buscarPorIdEUniversidadeDoFuncionarioLogado(alunoId); // Ensures it belongs to secretary's university

        // TODO: Comprehensive deletion logic:
        // 1. Disenroll from Turmas: Iterate turma.getAlunos().remove(aluno)
        // 2. Delete/Disable associated Usuario: 
        //    - If Usuario is only for this Aluno, can delete usuarioRepository.delete(aluno.getUsuario()).
        //    - Or mark Usuario as inactive.
        // 3. Handle other potential references.
        // For now, a basic deletion of Aluno which might leave Usuario orphaned.
        // A better approach for Usuario: If Aluno has a Usuario, fetch it and remove Aluno role. If no other roles, then delete/disable Usuario.

        Usuario usuarioDoAluno = aluno.getUsuario();
        alunoRepository.delete(aluno);

        if (usuarioDoAluno != null) {
            // Simplistic: if user has only ALUNO role and no other associations (e.g. not also a professor), delete user.
            // A more robust check would be needed in a real scenario.
            boolean podeDeletarUsuario = usuarioDoAluno.getPerfis().stream().anyMatch(p -> p.getNome() == Perfil.TipoPerfil.ROLE_ALUNO)
                                     && usuarioDoAluno.getPerfis().size() == 1;
            // And no other direct links from Funcionario, Professor, etc. tables to this Usuario ID.

            if (podeDeletarUsuario) {
                 // Check if this usuario is linked to other entities like Funcionario, Professor, Rep etc.
                if (!funcionarioRepository.findByUsuarioId(usuarioDoAluno.getId()).isPresent() &&
                    !professorRepository.findByUsuarioId(usuarioDoAluno.getId()).isPresent() &&
                    !representanteRepository.findByUsuarioId(usuarioDoAluno.getId()).isPresent()) {
                    usuarioRepository.delete(usuarioDoAluno);
                } else {
                    // If linked elsewhere, just remove ALUNO role or mark inactive
                    // For simplicity here, we don't fully implement this part to avoid too much complexity now
                    System.out.println("Usuário " + usuarioDoAluno.getEmail() + " associado a outras entidades, não será deletado mas o aluno foi.");
                }
            } else {
                 System.out.println("Usuário " + usuarioDoAluno.getEmail() + " tem outros perfis, não será deletado mas o aluno foi.");
            }
        }
    }

    private Funcionario getFuncionarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);
        // Assuming Funcionario is the role for Secretaria
        return funcionarioRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário logado não é um funcionário válido ou não encontrado."));
    }

    private Universidade getUniversidadeDoFuncionarioLogado() {
        Funcionario funcionario = getFuncionarioLogado();
        if (funcionario.getUniversidade() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Funcionário logado não está associado a nenhuma universidade.");
        }
        return funcionario.getUniversidade();
    }
}
