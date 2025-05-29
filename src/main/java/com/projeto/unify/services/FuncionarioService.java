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

@Service
@RequiredArgsConstructor
public class FuncionarioService {


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

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email do funcionário é obrigatório.");
        }

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado no sistema para um usuário.");
        }

        if (dto.getCpf() != null && funcionarioRepository.existsByCpf(dto.getCpf())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF de funcionário já cadastrado.");
        }

        Usuario novoUsuarioFuncionario = new Usuario();
        novoUsuarioFuncionario.setEmail(dto.getEmail());
        novoUsuarioFuncionario.setNome(dto.getNome() + " " + dto.getSobrenome());

        String senhaTemporaria = UUID.randomUUID().toString().substring(0, 8);
        novoUsuarioFuncionario.setSenha(passwordEncoder.encode(senhaTemporaria));
        novoUsuarioFuncionario.setPrimeiroAcesso(true);
        novoUsuarioFuncionario.setAtivo(true);

        Perfil perfilFuncionario = perfilService.obterOuCriarPerfil(Perfil.TipoPerfil.ROLE_FUNCIONARIO);
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
            if (emailService != null) {
                String nomeDestinatario = funcionario.getNomeCompleto();
                emailService.enviarCredenciaisAcesso(emailPessoal, emailInstitucional, senhaTemporaria, nomeDestinatario);
            }
        } catch (Exception e) {
            System.err.println("Falha ao enviar email de primeiro acesso para " + usuarioSalvo.getEmail() + ": " + e.getMessage());
        }

        return funcionarioSalvo;
    }

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


}
