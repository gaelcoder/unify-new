package com.projeto.unify.services;

import com.projeto.unify.dtos.RepresentanteDTO;
import com.projeto.unify.models.Perfil;
import com.projeto.unify.models.Representante;
import com.projeto.unify.models.Universidade;
import com.projeto.unify.models.Usuario;
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
    private final EmailService emailService;
    private final UsuarioRepository usuarioRepository;
    private final PerfilService perfilService;
    private final PasswordEncoder passwordEncoder; // Adicionado o PasswordEncoder

    public Representante criar(RepresentanteDTO dto) {
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

        representante.setNome(dto.getNome());
        representante.setSobrenome(dto.getSobrenome());
        representante.setEmail(dto.getEmail());
        representante.setTelefone(dto.getTelefone());
        representante.setCargo(dto.getCargo());

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
            representante.getUniversidade().setRepresentante(null);
            representante.setUniversidade(null);
        }

        representanteRepository.delete(representante);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
        String nome = representante.getNome().toLowerCase()
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

        String sobrenome = representante.getSobrenome().toLowerCase()
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

        // Pegar último sobrenome se houver espaços
        if (sobrenome.contains(" ")) {
            String[] partes = sobrenome.split(" ");
            sobrenome = partes[partes.length - 1];
        }

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

        return nome + "." + sobrenome + "@adm." + parteUniversidade + ".unify.edu.com";
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
            usuarioRepository.save(usuario);
        }

        // Desvincular da universidade
        representante.setUniversidade(null);
        representante.setUsuario(null); // Também limpar a referência ao usuário

        // Salvar e retornar representante
        return representanteRepository.save(representante);
    }

}