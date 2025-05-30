package com.projeto.unify.services;

import com.projeto.unify.dtos.FuncionarioDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.repositories.FuncionarioRepository;
import com.projeto.unify.repositories.RepresentanteRepository;
import com.projeto.unify.repositories.UsuarioRepository;
import com.projeto.unify.repositories.AlunoRepository;
import com.projeto.unify.repositories.ProfessorRepository;
import com.projeto.unify.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class FuncionarioService {

    private static final String SETOR_RH = "RH";
    private static final String SETOR_SECRETARIA = "Secretaria";
    private static final List<String> SETORES_VALIDOS = Arrays.asList(SETOR_RH, SETOR_SECRETARIA);

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
    public Funcionario criar(FuncionarioDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();

        Usuario adminUser = usuarioService.findByEmail(emailUsuarioLogado);

        Representante adminUniversidade = representanteRepository.findByUsuarioId(adminUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Usuário logado não é um representante de universidade válido."));

        Universidade universidadeDoAdmin = adminUniversidade.getUniversidade();
        if (universidadeDoAdmin == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Representante não está associado a nenhuma universidade.");
        }

        if (dto.getSetor() == null || dto.getSetor().isBlank() || !SETORES_VALIDOS.contains(dto.getSetor())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Setor do funcionário é obrigatório e deve ser 'RH' ou 'Secretaria'.");
        }

        String emailInstitucional = gerarEmailInstitucionalFuncionario(dto, universidadeDoAdmin, dto.getSetor());

        if (usuarioRepository.existsByEmail(emailInstitucional)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email institucional gerado já cadastrado no sistema.");
        }
        
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equals(emailInstitucional) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email pessoal informado já cadastrado no sistema para um usuário.");
        }

        // Validacao cruzada de Email Pessoal
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (alunoRepository.existsByEmail(dto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email pessoal já cadastrado para um aluno.");
            }
            if (professorRepository.existsByEmail(dto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email pessoal já cadastrado para um professor.");
            }
            if (representanteRepository.existsByEmail(dto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email pessoal já cadastrado para um representante.");
            }
            // Nao precisa checar funcionarioRepository.existsByEmail pois ja e' coberto pelo @Column(unique=true)
            // e pela verificacao de usuarioRepository.existsByEmail acima, assumindo que o email pessoal pode ser o mesmo do usuario.
        }

        // Validacao cruzada de Telefone
        if (dto.getTelefone() != null && !dto.getTelefone().isBlank()) {
            if (funcionarioRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado para um funcionário.");
            }
            if (alunoRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado para um aluno.");
            }
            if (professorRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado para um professor.");
            }
            if (representanteRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado para um representante.");
            }
        }

        if (dto.getCpf() != null) {
            if (funcionarioRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado para um funcionário.");
            }
            if (representanteRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado para um representante.");
            }
            if (alunoRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado para um aluno.");
            }
            if (professorRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado para um professor.");
            }
        }

        Usuario novoUsuarioFuncionario = new Usuario();
        novoUsuarioFuncionario.setEmail(emailInstitucional);
        novoUsuarioFuncionario.setNome(dto.getNome() + " " + dto.getSobrenome());
        novoUsuarioFuncionario.setPrimeiroAcesso(true);

        String senhaTemporaria = gerarSenhaAleatoria();
        novoUsuarioFuncionario.setSenha(passwordEncoder.encode(senhaTemporaria));

        Perfil perfilFuncionario;
        if (SETOR_RH.equals(dto.getSetor())) {
            perfilFuncionario = perfilService.obterOuCriarPerfil(Perfil.TipoPerfil.ROLE_FUNCIONARIO_RH);
        } else {
            perfilFuncionario = perfilService.obterOuCriarPerfil(Perfil.TipoPerfil.ROLE_FUNCIONARIO);
        }
        novoUsuarioFuncionario.setPerfis(Set.of(perfilFuncionario));

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuarioFuncionario);

        Funcionario funcionario = new Funcionario();
        funcionario.setCpf(dto.getCpf());
        funcionario.setDataNascimento(dto.getDataNascimento());
        funcionario.setNome(dto.getNome());
        funcionario.setSobrenome(dto.getSobrenome());
        funcionario.setEmail(dto.getEmail());
        funcionario.setTelefone(dto.getTelefone());
        funcionario.setSetor(dto.getSetor());
        funcionario.setSalario(dto.getSalario());
        funcionario.setUniversidade(universidadeDoAdmin);
        funcionario.setUsuario(usuarioSalvo);

        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

        try {
            String emailDestinatario = (dto.getEmail() != null && !dto.getEmail().isBlank()) ? dto.getEmail() : emailInstitucional;
            if (emailService != null) {
                String nomeDestinatario = funcionario.getNomeCompleto();
                emailService.enviarCredenciaisAcesso(emailDestinatario, emailInstitucional, senhaTemporaria, nomeDestinatario);
            }
        } catch (Exception e) {
            System.err.println("Falha ao enviar email de primeiro acesso para " + usuarioSalvo.getEmail() + ": " + e.getMessage());
        }

        return funcionarioSalvo;
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    private String gerarEmailInstitucionalFuncionario(FuncionarioDTO dto, Universidade universidade, String setor) {
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

        String infixEmail;
        if (SETOR_RH.equals(setor)) {
            infixEmail = "rh";
        } else {
            infixEmail = "sec";
        }

        return primeiroNome + "." + ultimoSobrenome + "@" + infixEmail + "." + parteUniversidade + ".unify.edu.com";
    }

    /**
     * Gera uma senha aleatória para primeiro acesso
     */
    private String gerarSenhaAleatoria() {
        // Implementação para gerar senha aleatória
        return UUID.randomUUID().toString().substring(0, 8);
    }

    // Helper method to get the university of the logged-in user (assuming Admin Universidade)
    private Universidade getUniversidadeDoUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);

        // Assuming an Admin Universidade is always linked via Representante
        // Adjust if other user types can manage funcionarios directly
        Representante representante = representanteRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, 
                                "Usuário não é um representante válido para acessar esta funcionalidade."));
        
        Universidade universidade = representante.getUniversidade();
        if (universidade == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                                "Representante não está associado a nenhuma universidade.");
        }
        return universidade;
    }

    @Transactional(readOnly = true)
    public List<Funcionario> listarTodosPorUniversidadeDoUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check if user is ADMIN_GERAL - if so, potentially list all or require a university filter
        // For now, if ADMIN_GERAL, this might return an empty list or throw error if not scoped.
        // This example scopes to ADMIN_UNIVERSIDADE
        boolean isAdminGeral = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN_GERAL"));

        if (isAdminGeral) {
            // ADMIN_GERAL: Decide strategy - list all, or require filter, or error for this specific method
            // For now, let's allow listing all if no specific university context for admin geral is enforced here
            return funcionarioRepository.findAll(); 
        }
        
        Universidade universidade = getUniversidadeDoUsuarioLogado();
        return funcionarioRepository.findByUniversidadeId(universidade.getId());
    }

    @Transactional(readOnly = true)
    public Funcionario buscarPorIdEUniversidadeDoUsuarioLogado(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdminGeral = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN_GERAL"));

        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado com ID: " + id));

        if (!isAdminGeral) {
            Universidade universidadeDoUsuario = getUniversidadeDoUsuarioLogado();
            if (!funcionario.getUniversidade().getId().equals(universidadeDoUsuario.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário não pertence à universidade do usuário logado.");
            }
        }
        return funcionario;
    }

    @Transactional
    public Funcionario atualizar(Long id, FuncionarioDTO dto) {
        Funcionario funcionarioExistente = buscarPorIdEUniversidadeDoUsuarioLogado(id);

        // Validar CPF apenas se estiver sendo alterado e for diferente do existente
        if (dto.getCpf() != null && !dto.getCpf().isBlank() && !dto.getCpf().equals(funcionarioExistente.getCpf())) {
            if (funcionarioRepository.existsByCpf(dto.getCpf()) ||
                alunoRepository.existsByCpf(dto.getCpf()) ||
                professorRepository.existsByCpf(dto.getCpf()) ||
                representanteRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado no sistema.");
            }
            funcionarioExistente.setCpf(dto.getCpf());
        }

        // Validar Email Pessoal apenas se estiver sendo alterado e for diferente do existente
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equals(funcionarioExistente.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getEmail()) || // Checa se já existe como email de algum usuário (institucional ou pessoal)
                alunoRepository.existsByEmail(dto.getEmail()) ||
                professorRepository.existsByEmail(dto.getEmail()) ||
                representanteRepository.existsByEmail(dto.getEmail()) ||
                funcionarioRepository.existsByEmail(dto.getEmail())) { // Checa outros funcionários
                // Para evitar auto-colisão na propria entidade ao atualizar, precisamos de uma logica mais especifica
                // if (funcionarioRepository.findByEmail(dto.getEmail()).filter(f -> !f.getId().equals(id)).isPresent()) {
                // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email pessoal já cadastrado para outro funcionário.");
                // }
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email pessoal já cadastrado no sistema.");
            }
            funcionarioExistente.setEmail(dto.getEmail());
        }

        // Validar Telefone apenas se estiver sendo alterado e for diferente do existente
        if (dto.getTelefone() != null && !dto.getTelefone().isBlank() && !dto.getTelefone().equals(funcionarioExistente.getTelefone())) {
            // Para evitar auto-colisao na propria entidade ao atualizar, a query no repositorio precisaria excluir o ID atual
            // Ex: existsByTelefoneAndIdNot(String telefone, Long id);
            if (funcionarioRepository.existsByTelefone(dto.getTelefone()) || // Esta checagem pode dar falso positivo ao atualizar o mesmo funcionario
                alunoRepository.existsByTelefone(dto.getTelefone()) ||
                professorRepository.existsByTelefone(dto.getTelefone()) ||
                representanteRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado no sistema.");
            }
            funcionarioExistente.setTelefone(dto.getTelefone());
        }
        
        // Atualiza o email do usuario associado SE o email institucional mudar (não é o caso aqui, mas é bom ter em mente)
        // Basic updates for now. More complex logic (e.g., email change on setor change) can be added.
        funcionarioExistente.setNome(dto.getNome());
        funcionarioExistente.setSobrenome(dto.getSobrenome());
        funcionarioExistente.setDataNascimento(dto.getDataNascimento());
        funcionarioExistente.setSalario(dto.getSalario());

        // If setor is being changed, new role and potentially new email logic might be needed.
        // For simplicity, we assume email is not re-generated on update for now unless setor changes.
        if (dto.getSetor() != null && !dto.getSetor().equals(funcionarioExistente.getSetor())) {
            if (!SETORES_VALIDOS.contains(dto.getSetor())) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Setor inválido.");
            }
            funcionarioExistente.setSetor(dto.getSetor());
            
            // Update user role based on new setor
            Usuario usuarioDoFuncionario = funcionarioExistente.getUsuario();
            Perfil novoPerfil;
            if (SETOR_RH.equals(dto.getSetor())) {
                novoPerfil = perfilService.obterOuCriarPerfil(Perfil.TipoPerfil.ROLE_FUNCIONARIO_RH);
            } else { // SETOR_SECRETARIA
                novoPerfil = perfilService.obterOuCriarPerfil(Perfil.TipoPerfil.ROLE_FUNCIONARIO);
            }
            usuarioDoFuncionario.setPerfis(Set.of(novoPerfil)); // Replace existing roles
            usuarioRepository.save(usuarioDoFuncionario);
        }
        
        return funcionarioRepository.save(funcionarioExistente);
    }

    @Transactional
    public void deletar(Long id) {
        Funcionario funcionarioParaDeletar = buscarPorIdEUniversidadeDoUsuarioLogado(id); // Ensures user has access
        Usuario usuarioAssociado = funcionarioParaDeletar.getUsuario();

        funcionarioRepository.delete(funcionarioParaDeletar);

        if (usuarioAssociado != null) {
            // Decide strategy: soft delete user, hard delete, or disassociate profiles.
            // For now, simple deletion of the user too. This might need to be more nuanced.
            usuarioRepository.delete(usuarioAssociado);
        }
    }

}
