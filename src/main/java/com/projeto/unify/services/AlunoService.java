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

import java.time.LocalDate;
import java.time.Month;
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
    private final GraduacaoRepository graduacaoRepository;
    private final TurmaRepository turmaRepository;

    private Funcionario getFuncionarioSecretariaLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);

        boolean isFuncionarioSecretaria = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Perfil.TipoPerfil.ROLE_FUNCIONARIO.toString()));

        if (!isFuncionarioSecretaria) {
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não tem permissão de funcionário da secretaria.");
        }

        return funcionarioRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário da secretaria não encontrado ou não associado ao usuário logado."));
    }

    @Transactional
    public Aluno criar(AlunoDTO dto) { 
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);
        
        Funcionario funcionarioCriador = funcionarioRepository.findByUsuarioId(usuarioLogado.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário logado não é um funcionário válido para criar alunos."));
        Universidade universidadeDoAluno = funcionarioCriador.getUniversidade();
            if (universidadeDoAluno == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Funcionário não está associado a nenhuma universidade.");
        }

        if (dto.getCpf() != null && !dto.getCpf().isBlank()) {
            if (alunoRepository.existsByCpf(dto.getCpf()) ||
                    funcionarioRepository.existsByCpf(dto.getCpf()) ||
                    professorRepository.existsByCpf(dto.getCpf()) ||
                    representanteRepository.existsByCpf(dto.getCpf())
            ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado no sistema.");
            }
        }

        String emailAlunoPessoal = dto.getEmail();
        if (emailAlunoPessoal != null && !emailAlunoPessoal.isBlank()) {
            if (usuarioRepository.existsByEmail(emailAlunoPessoal) ||
                alunoRepository.existsByEmail(emailAlunoPessoal) ||
                funcionarioRepository.existsByEmail(emailAlunoPessoal) ||
                professorRepository.existsByEmail(emailAlunoPessoal)
            ) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email pessoal já cadastrado no sistema.");
            }
        }

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
        if (usuarioRepository.existsByEmail(emailInstitucional)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email institucional gerado ("+ emailInstitucional +") já existe. Tente novamente ou verifique os dados.");
        }
        
        Usuario novoUsuarioAluno = new Usuario();
        novoUsuarioAluno.setEmail(emailInstitucional);
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
        aluno.setEmail(emailAlunoPessoal);
        aluno.setTelefone(dto.getTelefone());
        LocalDate hoje = LocalDate.now();
        String ano = String.valueOf(hoje.getYear()).substring(2);
        String semestre = (hoje.getMonthValue() <= 6) ? "10" : "20";

        Graduacao graduacaoDoAluno = graduacaoRepository.findById(dto.getGraduacaoId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Graduação não encontrada. ID: " + dto.getGraduacaoId()));
        String graduacaoIdString = String.valueOf(graduacaoDoAluno.getId());

        long numeroDeAlunosNaGraduacaoNaUniversidade = alunoRepository.countByUniversidadeAndGraduacao(universidadeDoAluno, graduacaoDoAluno);
        String numeroSequencial = String.format("%04d", numeroDeAlunosNaGraduacaoNaUniversidade + 1);

        String matriculaGerada = ano + semestre + graduacaoIdString + numeroSequencial;
        aluno.setMatricula(matriculaGerada);
        aluno.setCr(0.0f);
        aluno.setUniversidade(universidadeDoAluno);
        aluno.setUsuario(usuarioSalvo);
        aluno.setGraduacao(graduacaoDoAluno);
        
        Aluno alunoSalvo = alunoRepository.save(aluno);

        try {
            if (emailService != null && emailInstitucional != null && !emailInstitucional.isBlank()) {
                emailService.enviarCredenciaisAcesso(emailAlunoPessoal != null && !emailAlunoPessoal.isBlank() ? emailAlunoPessoal : emailInstitucional, emailInstitucional, senhaTemporaria, aluno.getNomeCompleto());
            }
        } catch (Exception e) {
            System.err.println("Falha ao enviar email de primeiro acesso para " + emailInstitucional + ": " + e.getMessage());
        }
        return alunoSalvo;
    }

    @Transactional(readOnly = true)
    public List<Aluno> listarAlunosDaUniversidadeLogada() {
        Funcionario funcionarioLogado = getFuncionarioSecretariaLogado();
        if (funcionarioLogado.getUniversidade() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Funcionário não está associado a nenhuma universidade.");
        }
        return alunoRepository.findByUniversidade(funcionarioLogado.getUniversidade());
    }

    @Transactional(readOnly = true)
    public Aluno buscarAlunoPorIdDaUniversidadeLogada(Long alunoId) {
        Funcionario funcionarioLogado = getFuncionarioSecretariaLogado();
        Universidade universidadeFuncionario = funcionarioLogado.getUniversidade();
        if (universidadeFuncionario == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Funcionário não está associado a nenhuma universidade.");
        }

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado com ID: " + alunoId));

        if (!aluno.getUniversidade().equals(universidadeFuncionario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não tem permissão para acessar este aluno de outra universidade.");
        }
        return aluno;
    }

    @Transactional
    public Aluno atualizarAluno(Long alunoId, AlunoDTO alunoDTO) {
        Funcionario funcionarioLogado = getFuncionarioSecretariaLogado();
        Universidade universidadeFuncionario = funcionarioLogado.getUniversidade();
        if (universidadeFuncionario == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Funcionário não está associado a nenhuma universidade.");
        }

        Aluno alunoExistente = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado com ID: " + alunoId));

        if (!alunoExistente.getUniversidade().equals(universidadeFuncionario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não tem permissão para atualizar este aluno de outra universidade.");
        }

        if (alunoDTO.getCpf() != null && !alunoDTO.getCpf().isBlank() && !alunoDTO.getCpf().equals(alunoExistente.getCpf())) {
            if (alunoRepository.existsByCpf(alunoDTO.getCpf()) || funcionarioRepository.existsByCpf(alunoDTO.getCpf()) || professorRepository.existsByCpf(alunoDTO.getCpf()) || representanteRepository.existsByCpf(alunoDTO.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Novo CPF já cadastrado no sistema.");
            }
            alunoExistente.setCpf(alunoDTO.getCpf());
        }

        if (alunoDTO.getEmail() != null && !alunoDTO.getEmail().isBlank() && !alunoDTO.getEmail().equals(alunoExistente.getEmail())) {
            boolean emailJaExiste = false;
            if (usuarioRepository.existsByEmailExcludingUsuarioId(alunoDTO.getEmail(), alunoExistente.getUsuario().getId())) {
                emailJaExiste = true;
            }
            if (!emailJaExiste && alunoRepository.existsByEmail(alunoDTO.getEmail())) {
                emailJaExiste = true;
            }
            if (!emailJaExiste && funcionarioRepository.existsByEmail(alunoDTO.getEmail())) {
                emailJaExiste = true;
            }
            if (!emailJaExiste && professorRepository.existsByEmail(alunoDTO.getEmail())) {
                emailJaExiste = true;
            }

            if (emailJaExiste) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Novo email pessoal já informado está em uso por outro perfil no sistema.");
            }
            alunoExistente.setEmail(alunoDTO.getEmail());
        }

        if (alunoDTO.getTelefone() != null && !alunoDTO.getTelefone().isBlank() && !alunoDTO.getTelefone().equals(alunoExistente.getTelefone())) {
            boolean telefoneJaExiste = false;
            if (alunoRepository.existsByTelefone(alunoDTO.getTelefone())){
                telefoneJaExiste = true;
            }
            if (!telefoneJaExiste && funcionarioRepository.existsByTelefone(alunoDTO.getTelefone())) {
                telefoneJaExiste = true;
            }
            if (!telefoneJaExiste && professorRepository.existsByTelefone(alunoDTO.getTelefone())) {
                telefoneJaExiste = true;
            }

            if (telefoneJaExiste) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Novo telefone já informado está em uso por outro perfil no sistema.");
            }
            alunoExistente.setTelefone(alunoDTO.getTelefone());
        }

        if (!alunoExistente.getNomeCompleto().equals(alunoDTO.getNome() + " " + alunoDTO.getSobrenome())) {
            Usuario usuarioAluno = alunoExistente.getUsuario();
            if (usuarioAluno != null) {
                usuarioAluno.setNome(alunoDTO.getNome() + " " + alunoDTO.getSobrenome());
                usuarioRepository.save(usuarioAluno);
            }
        }

        if (alunoDTO.getGraduacaoId() != null && (alunoExistente.getGraduacao() == null || !alunoDTO.getGraduacaoId().equals(alunoExistente.getGraduacao().getId()))) {
            Graduacao novaGraduacao = graduacaoRepository.findById(alunoDTO.getGraduacaoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Graduação para atualização não encontrada."));
            if(!novaGraduacao.getUniversidade().equals(universidadeFuncionario)){
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova graduação não pertence à universidade do aluno.");
            }
            alunoExistente.setGraduacao(novaGraduacao);
        }

        return alunoRepository.save(alunoExistente);
    }

    @Transactional
    public void deletarAluno(Long alunoId) {
        Funcionario funcionarioLogado = getFuncionarioSecretariaLogado();
        Universidade universidadeFuncionario = funcionarioLogado.getUniversidade();
         if (universidadeFuncionario == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Funcionário não está associado a nenhuma universidade.");
        }

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado com ID: " + alunoId));

        if (!aluno.getUniversidade().equals(universidadeFuncionario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não tem permissão para deletar este aluno de outra universidade.");
        }

        Usuario usuarioAssociado = aluno.getUsuario();
        alunoRepository.delete(aluno);

        if (usuarioAssociado != null) {
            usuarioRepository.delete(usuarioAssociado);
        }
    }

    private String gerarSenhaAleatoria() {
        return UUID.randomUUID().toString().substring(0, 8);
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
        
        int tentativas = 0;
        String emailBase = primeiroNome + "." + ultimoSobrenome;
        String sufixoDominio = "@" + infixEmail + "." + parteUniversidade + ".unify.edu.com";
        String emailGerado = emailBase + sufixoDominio;

        while(usuarioRepository.existsByEmail(emailGerado) && tentativas < 100) {
            tentativas++;
            emailGerado = emailBase + tentativas + sufixoDominio;
        }
        if (usuarioRepository.existsByEmail(emailGerado)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível gerar um email institucional único.");
        }
        return emailGerado;
    }
}
