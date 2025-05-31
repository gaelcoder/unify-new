package com.projeto.unify.services;

import com.projeto.unify.dtos.RepresentanteDTO;
import com.projeto.unify.models.Perfil;
import com.projeto.unify.models.Representante;
import com.projeto.unify.models.Universidade;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.repositories.FuncionarioRepository;
import com.projeto.unify.repositories.AlunoRepository;
import com.projeto.unify.repositories.ProfessorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.projeto.unify.repositories.RepresentanteRepository;
import com.projeto.unify.repositories.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepresentanteService {

    private final UsuarioService usuarioService;
    private final RepresentanteRepository representanteRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final EmailService emailService;
    private final UsuarioRepository usuarioRepository;
    private final PerfilService perfilService;
    private final PasswordEncoder passwordEncoder; // Adicionado o PasswordEncoder

    public Representante criar(RepresentanteDTO dto) {
        // Ensure HttpStatus, ResponseStatusException are imported
        if (dto.getCpf() != null && !dto.getCpf().isBlank()) {
            if (funcionarioRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado para um funcionário.");
            }
            if (representanteRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado para um representante.");
            }
            if (alunoRepository.existsByCpf(dto.getCpf())) { // Added check
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado para um aluno.");
            }
            if (professorRepository.existsByCpf(dto.getCpf())) { // Added check
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado para um professor.");
            }
        }

        // Email Validation
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            // Check across all user emails (institutional or personal login for other entities)
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado para um usuário no sistema.");
            }
            if (representanteRepository.existsByEmail(dto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado para um representante.");
            }
            if (alunoRepository.existsByEmail(dto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado para um aluno.");
            }
            if (funcionarioRepository.existsByEmail(dto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado para um funcionário.");
            }
            if (professorRepository.existsByEmail(dto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado para um professor.");
            }
        }

        // Telefone Validation
        if (dto.getTelefone() != null && !dto.getTelefone().isBlank()) {
            if (representanteRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado para um representante.");
            }
            if (alunoRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado para um aluno.");
            }
            if (funcionarioRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado para um funcionário.");
            }
            if (professorRepository.existsByTelefone(dto.getTelefone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado para um professor.");
            }
        }

        Representante representante = new Representante(
                dto.getCpf(),
                dto.getDataNascimento(),
                dto.getNome(),
                dto.getSobrenome(),
                dto.getEmail(),
                dto.getTelefone(),
                dto.getCargo()
        );
        return representanteRepository.save(representante);
    }

    public Representante atualizar(Long id, RepresentanteDTO dto) {
        Representante representante = buscarPorId(id);

        // CPF Validation (if changed)
        if (dto.getCpf() != null && !dto.getCpf().isBlank() && !dto.getCpf().equals(representante.getCpf())) {
            if (funcionarioRepository.existsByCpf(dto.getCpf()) ||
                alunoRepository.existsByCpf(dto.getCpf()) ||
                professorRepository.existsByCpf(dto.getCpf()) ||
                representanteRepository.findByCpf(dto.getCpf()).filter(r -> !r.getId().equals(id)).isPresent()) { // Check other reps
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado no sistema.");
            }
            representante.setCpf(dto.getCpf()); // Set CPF only if it passes validation and is different
        }

        // Email Validation (if changed)
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equals(representante.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getEmail()) || // Check all user accounts
                alunoRepository.existsByEmail(dto.getEmail()) ||
                funcionarioRepository.existsByEmail(dto.getEmail()) ||
                professorRepository.existsByEmail(dto.getEmail()) ||
                representanteRepository.findByEmail(dto.getEmail()).filter(r -> !r.getId().equals(id)).isPresent()) { // Check other reps
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado no sistema.");
            }
            representante.setEmail(dto.getEmail());
            // If this email is used for login, update Usuario entity as well
            Usuario usuarioDoRepresentante = representante.getUsuario();
            if (usuarioDoRepresentante != null && !usuarioDoRepresentante.getEmail().equals(dto.getEmail())){
                // Note: If the representante's email for login is their *personal* email (from RepresentanteDTO)
                // and NOT the generated institutional one, this logic needs to be certain.
                // The gerarEmailInstitucional is separate. This implies Representante logs in with this dto.getEmail().
                if(usuarioRepository.existsByEmail(dto.getEmail()) && !usuarioDoRepresentante.getEmail().equals(dto.getEmail())){
                     // This check is a bit redundant if the above checks are comprehensive for dto.getEmail() across all users
                     // but ensures the target email for the Usuario object isn't already taken by ANOTHER user.
                     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email de usuário já existe.");
                }
                usuarioDoRepresentante.setEmail(dto.getEmail());
                usuarioRepository.save(usuarioDoRepresentante);
            }
        }

        // Telefone Validation (if changed)
        if (dto.getTelefone() != null && !dto.getTelefone().isBlank() && !dto.getTelefone().equals(representante.getTelefone())) {
            if (alunoRepository.existsByTelefone(dto.getTelefone()) ||
                funcionarioRepository.existsByTelefone(dto.getTelefone()) ||
                professorRepository.existsByTelefone(dto.getTelefone()) ||
                representanteRepository.findByTelefone(dto.getTelefone()).filter(r -> !r.getId().equals(id)).isPresent()) { // Check other reps
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado no sistema.");
            }
            representante.setTelefone(dto.getTelefone());
        }

        representante.setNome(dto.getNome());
        representante.setSobrenome(dto.getSobrenome());
        // Email and Telefone are set above after validation
        representante.setCargo(dto.getCargo());
        // DataNascimento is not in DTO for update in current snippet, if it was, it would be here.

        return representanteRepository.save(representante);
    }

    public List<Representante> listarTodos() {
        return representanteRepository.findAll();
    }

    public List<Representante> listarDisponiveis() {
        return representanteRepository.findAll().stream()
                .filter(r -> r.getUniversidade() == null)
                .collect(Collectors.toList());
    }

    public Representante buscarPorId(Long id) {
        return representanteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Representante não encontrado"));
    }

    public void excluir(Long id) {
        Representante representante = buscarPorId(id);

        // Se estiver associado a uma universidade, desassociar primeiro
        if (representante.getUniversidade() != null) {
            // It's important that the University entity is also saved if its state changes.
            // Assuming the ORM handles this or it's done in another part of the transaction.
            representante.getUniversidade().setRepresentante(null);
            // representante.setUniversidade(null); // This is done by desassociarRepresentante

        }

        // Deactivate and disassociate the user
        desassociarRepresentante(representante);


        // Finally, delete the representante record
        representanteRepository.delete(representante);
    }

    @Transactional
    public Representante associarRepresentante(Universidade universidade, Representante representante, String emailPessoal) {
        // Preservar lógica existente de associação
        representante.setUniversidade(universidade);

        // Gerar email institucional no formato nome.sobrenome@adm.unify.nomeuniversidade.edu.com
        String emailInstitucional = gerarEmailInstitucional(representante, universidade);

        // Adicionar lógica de gerenciamento de usuário
        Usuario usuario = usuarioRepository.findByEmail(emailInstitucional).orElse(null);

        if (usuario == null) {
            // Criar novo usuário se não existir
            usuario = new Usuario();
            usuario.setNome(representante.getNome());
            usuario.setEmail(emailInstitucional);

            // Gerar senha aleatória para primeiro acesso
            String senhaTemporaria = gerarSenhaAleatoria();
            usuario.setSenha(passwordEncoder.encode(senhaTemporaria));
            usuario.setPrimeiroAcesso(true);

            // Adicionar perfil de admin da universidade
            Perfil perfilAdmin = perfilService.obterOuCriarPerfil(Perfil.TipoPerfil.ROLE_ADMIN_UNIVERSIDADE);
            usuario.adicionarPerfil(perfilAdmin);

            usuarioRepository.save(usuario);

            // Enviar credenciais por email se o serviço estiver disponível
            if (emailService != null) {
                String nomeDestinatario = representante.getNomeCompleto();
                emailService.enviarCredenciaisAcesso(emailPessoal, emailInstitucional, senhaTemporaria, nomeDestinatario);
            }
        } else {
            // Se usuário já existir, apenas ativá-lo e garantir que tenha o perfil correto
            usuario.setAtivo(true);

            // Verificar se já tem o perfil necessário
            boolean temPerfilAdmin = usuario.getPerfis().stream()
                    .anyMatch(p -> p.getNome() == Perfil.TipoPerfil.ROLE_ADMIN_UNIVERSIDADE);

            if (!temPerfilAdmin) {
                Perfil perfilAdmin = perfilService.obterOuCriarPerfil(Perfil.TipoPerfil.ROLE_ADMIN_UNIVERSIDADE);
                usuario.adicionarPerfil(perfilAdmin);
            }

            usuarioRepository.save(usuario);
        }

        // Associar usuário ao representante
        representante.setUsuario(usuario);

        // Salvar e retornar representante
        return representanteRepository.save(representante);
    }

    /**
     * Gera um email institucional no formato nome.sobrenome@adm.unify.nomeuniversidade.edu.com
     * Se o nome da universidade for maior que 10 caracteres, usa a sigla
     */
    private String gerarEmailInstitucional(Representante representante, Universidade universidade) {
        String primeiroNome = representante.getNome().toLowerCase()
                .split(" ")[0]
                .replace(" ", "")
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

        String ultimoSobrenome = representante.getSobrenome().toLowerCase();
        if (ultimoSobrenome.contains(" ")) {
            String[] partes = ultimoSobrenome.split(" ");
            ultimoSobrenome = partes[partes.length - 1];
        }
        ultimoSobrenome = ultimoSobrenome
                .replace(" ", "")
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

        // Determinar parte da universidade no email (nome ou sigla)
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

        return primeiroNome + "." + ultimoSobrenome + "@adm." + parteUniversidade + ".unify.edu.com";
    }

    /**
     * Gera uma senha aleatória para primeiro acesso
     */
    private String gerarSenhaAleatoria() {
        // Implementação para gerar senha aleatória
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Representante desassociarRepresentante(Representante representante) {
        // Desativar conta de usuário
        Usuario usuario = representante.getUsuario();
        if (usuario != null) {
            usuario.setAtivo(false);
            // Optionally, remove specific profiles related to this role
            // Example: usuario.getPerfis().removeIf(perfil -> perfil.getNome() == Perfil.TipoPerfil.ROLE_ADMIN_UNIVERSIDADE);
            usuarioRepository.save(usuario);
        }

        // Desvincular da universidade
        if (representante.getUniversidade() != null) {
            representante.getUniversidade().setRepresentante(null); // Ensure bi-directional relationship is cleared
            // Consider saving the universidade entity if changes to it need to be persisted immediately
            // e.g., universidadeRepository.save(representante.getUniversidade());
        }
        representante.setUniversidade(null);
        representante.setUsuario(null); // Também limpar a referência ao usuário

        // Salvar e retornar representante
        return representanteRepository.save(representante);
    }

}
