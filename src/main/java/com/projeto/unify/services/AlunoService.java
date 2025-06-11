package com.projeto.unify.services;

import com.projeto.unify.dtos.AlunoDTO;
import com.projeto.unify.dtos.ProfessorDTO;
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
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final EntityManager entityManager;
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

        if (universidadeDoAluno == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não foi possível determinar a universidade para o aluno. Verifique se o usuário logado (funcionário/admin universidade) está corretamente associado a uma universidade ou forneça uma ID de universidade no DTO.");
        }

        if (dto.getGraduacaoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID da Graduação é obrigatório.");
        }
        Graduacao graduacao = graduacaoRepository.findById(dto.getGraduacaoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Graduação não encontrada com o ID fornecido."));

        // Ensure the Graduacao belongs to the Aluno's Universidade
        if (!graduacao.getUniversidade().equals(universidadeDoAluno)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A Graduação especificada não pertence à universidade do aluno.");
        }

        if (dto.getCampus() == null || dto.getCampus().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O campus é obrigatório.");
        }

        if (!graduacao.getCampusDisponiveis().contains(dto.getCampus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O campus '" + dto.getCampus() + "' não está disponível para a graduação '" + graduacao.getTitulo() + "'.");
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

        String emailInstitucional = gerarEmailInstitucionalAluno(dto, universidadeDoAluno);

        Usuario novoUsuarioAluno = new Usuario();
        novoUsuarioAluno.setEmail(emailInstitucional);
        novoUsuarioAluno.setNome(dto.getNome() + " " + dto.getSobrenome());
        novoUsuarioAluno.setPrimeiroAcesso(true);
        String senhaTemporaria = gerarSenhaAleatoria();
        novoUsuarioAluno.setSenha(passwordEncoder.encode(senhaTemporaria));
        Perfil perfilAluno = perfilService.obterOuCriarPerfil(Perfil.TipoPerfil.ROLE_ALUNO);
        novoUsuarioAluno.adicionarPerfil(perfilAluno);
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
        aluno.setCampus(dto.getCampus());

        Aluno alunoSalvo = alunoRepository.save(aluno);

        try {
            if (emailService != null && emailAluno != null && !emailAluno.isBlank()) {
                emailService.enviarCredenciaisAcesso(emailAluno, emailInstitucional, senhaTemporaria, aluno.getNomeCompleto());
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

    public List<Map<String, Object>> listarAlunosComGraduacao() {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        List<Aluno> alunos = alunoRepository.findByUniversidadeWithGraduacao(universidade);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Aluno aluno : alunos) {
            Map<String, Object> alunoMap = new HashMap<>();
            alunoMap.put("id", aluno.getId());
            alunoMap.put("cpf", aluno.getCpf());
            alunoMap.put("dataNascimento", aluno.getDataNascimento());
            alunoMap.put("nome", aluno.getNome());
            alunoMap.put("sobrenome", aluno.getSobrenome());
            alunoMap.put("email", aluno.getEmail());
            alunoMap.put("telefone", aluno.getTelefone());
            alunoMap.put("matricula", aluno.getMatricula());
            alunoMap.put("cr", aluno.getCr());
            alunoMap.put("campus", aluno.getCampus());

            if (aluno.getGraduacao() != null) {
                Map<String, Object> graduacaoMap = new HashMap<>();
                graduacaoMap.put("id", aluno.getGraduacao().getId());
                graduacaoMap.put("titulo", aluno.getGraduacao().getTitulo());
                alunoMap.put("graduacao", graduacaoMap);
            } else {
                alunoMap.put("graduacao", null);
            }

            result.add(alunoMap);
        }
        return result;
    }

    public Aluno buscarPorIdEUniversidadeDoFuncionarioLogado(Long id) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        return alunoRepository.findByIdAndUniversidade(id, universidade)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado ou não pertence à sua universidade."));
    }

    public Map<String, Object> buscarAlunoDTOPorId(Long id) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Aluno aluno = alunoRepository.findByIdAndUniversidade(id, universidade)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado ou não pertence à sua universidade."));

        Map<String, Object> alunoMap = new HashMap<>();
        alunoMap.put("id", aluno.getId());
        alunoMap.put("cpf", aluno.getCpf());
        alunoMap.put("dataNascimento", aluno.getDataNascimento());
        alunoMap.put("nome", aluno.getNome());
        alunoMap.put("sobrenome", aluno.getSobrenome());
        alunoMap.put("email", aluno.getEmail());
        alunoMap.put("telefone", aluno.getTelefone());
        alunoMap.put("matricula", aluno.getMatricula());
        alunoMap.put("cr", aluno.getCr());
        alunoMap.put("campus", aluno.getCampus());

        if (aluno.getGraduacao() != null) {
            Map<String, Object> graduacaoMap = new HashMap<>();
            graduacaoMap.put("id", aluno.getGraduacao().getId());
            graduacaoMap.put("titulo", aluno.getGraduacao().getTitulo());
            graduacaoMap.put("campusDisponiveis", aluno.getGraduacao().getCampusDisponiveis());
            alunoMap.put("graduacao", graduacaoMap);
        } else {
            alunoMap.put("graduacao", null);
        }
        return alunoMap;
    }

    @Transactional
    public Aluno atualizar(Long id, AlunoDTO dto) {
        Universidade universidade = getUniversidadeDoFuncionarioLogado();
        Aluno aluno = alunoRepository.findByIdAndUniversidade(id, universidade)
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
            if (!novaGraduacao.getUniversidade().equals(universidade)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova graduação especificada não pertence à sua universidade.");
            }
            aluno.setGraduacao(novaGraduacao);
            if (dto.getCampus() != null && !dto.getCampus().isBlank()) {
                if (novaGraduacao.getCampusDisponiveis().contains(dto.getCampus())) {
                    aluno.setCampus(dto.getCampus());
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O campus '" + dto.getCampus() + "' não está disponível para a nova graduação.");
                }
            } else {
                aluno.setCampus(null);
            }
        } else if (dto.getCampus() != null && !dto.getCampus().equals(aluno.getCampus())) {
            if (aluno.getGraduacao().getCampusDisponiveis().contains(dto.getCampus())) {
                aluno.setCampus(dto.getCampus());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O campus '" + dto.getCampus() + "' não está disponível para a graduação atual.");
            }
        }

        return alunoRepository.save(aluno);
    }

    @Transactional
    public void deletar(Long alunoId) {
        Aluno aluno = buscarPorIdEUniversidadeDoFuncionarioLogado(alunoId);
        Usuario usuarioDoAluno = aluno.getUsuario();
        alunoRepository.delete(aluno);

        if (usuarioDoAluno != null) {
            boolean podeDeletarUsuario = usuarioDoAluno.getPerfis().stream().anyMatch(p -> p.getNome() == Perfil.TipoPerfil.ROLE_ALUNO)
                                     && usuarioDoAluno.getPerfis().size() == 1;
            // And no other direct links from Funcionario, Professor, etc. tables to this Usuario ID.

            if (podeDeletarUsuario) {
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


    private String gerarEmailInstitucionalAluno(AlunoDTO dto, Universidade universidade) {
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

        String infixEmail = "al";
        return primeiroNome + "." + ultimoSobrenome + "@" + infixEmail + "." + parteUniversidade + ".unify.edu.com";
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
