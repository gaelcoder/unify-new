package com.projeto.unify.services;

import com.projeto.unify.dtos.FuncionarioDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.repositories.FuncionarioRepository;
import com.projeto.unify.repositories.RepresentanteRepository;
import com.projeto.unify.repositories.UsuarioRepository;
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

        if (dto.getCpf() != null && funcionarioRepository.existsByCpf(dto.getCpf())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF de funcionário já cadastrado.");
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
        Funcionario funcionarioExistente = buscarPorIdEUniversidadeDoUsuarioLogado(id); // Ensures user has access

        // Basic updates for now. More complex logic (e.g., email change on setor change) can be added.
        funcionarioExistente.setNome(dto.getNome());
        funcionarioExistente.setSobrenome(dto.getSobrenome());
        funcionarioExistente.setDataNascimento(dto.getDataNascimento());
        funcionarioExistente.setCpf(dto.getCpf()); // Consider CPF uniqueness validation if it can change
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
        
        // Personal email update
        funcionarioExistente.setEmail(dto.getEmail());

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
