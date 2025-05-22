package com.projeto.unify.services;

import com.projeto.unify.models.Perfil;
import com.projeto.unify.models.Representante;
import com.projeto.unify.models.Universidade;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.repositories.PerfilRepository;
import com.projeto.unify.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    // Para inicialização do sistema - criar administrador geral
    @Transactional
    public Usuario criarAdminGeral(String email, String senha, boolean primeiroAcesso) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado");
        }

        Perfil perfilAdmin = obterOuCriarPerfil(Perfil.TipoPerfil.ROLE_ADMIN_GERAL);

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setPrimeiroAcesso(primeiroAcesso);
        usuario.getPerfis().add(perfilAdmin);

        return usuarioRepository.save(usuario);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    // Para criar administrador de universidade vinculado a um representante
    @Transactional
    public Usuario criarAdminUniversidade(Representante representante, Universidade universidade) {
        // Gerar email institucional para o administrador da universidade
        String emailAdmin = gerarEmailAdminUniversidade(representante, universidade);

        // Verificar se já existe
        if (usuarioRepository.existsByEmail(emailAdmin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Já existe um administrador para esta universidade");
        }

        // Gerar senha temporária
        String senhaTemporaria = gerarSenhaTemporaria();

        Perfil perfilAdminUniv = obterOuCriarPerfil(Perfil.TipoPerfil.ROLE_ADMIN_UNIVERSIDADE);

        // Criar usuário
        Usuario usuario = new Usuario();
        usuario.setEmail(emailAdmin);
        usuario.setSenha(passwordEncoder.encode(senhaTemporaria));
        usuario.setPrimeiroAcesso(true); // Forçar troca de senha no primeiro acesso
        usuario.getPerfis().add(perfilAdminUniv);

        usuario = usuarioRepository.save(usuario);

        // Enviar email com as credenciais
        emailService.enviarCredenciaisAcesso(
                representante.getEmail(), // Email pessoal do representante
                emailAdmin,               // Email institucional criado
                senhaTemporaria,          // Senha temporária
                representante.getNome()   // Nome para personalização
        );

        return usuario;
    }

    private String gerarEmailAdminUniversidade(Representante representante, Universidade universidade) {
        String nomeNormalizado = normalizarParaEmail(representante.getNome());
        String sobrenomeNormalizado = normalizarParaEmail(representante.getSobrenome());
        String universidadeNormalizada = normalizarParaEmail(universidade.getNome());

        return String.format("%s.%s@adm.%s.unify.edu.com",
                nomeNormalizado, sobrenomeNormalizado, universidadeNormalizada);
    }

    private String normalizarParaEmail(String texto) {
        return texto.toLowerCase()
                .replaceAll("[áàãâä]", "a")
                .replaceAll("[éèêë]", "e")
                .replaceAll("[íìîï]", "i")
                .replaceAll("[óòõôö]", "o")
                .replaceAll("[úùûü]", "u")
                .replaceAll("[ç]", "c")
                .replaceAll("[^a-z0-9]", "");
    }

    private String gerarSenhaTemporaria() {
        // Gerar senha aleatória de 10 caracteres
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder senha = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 10; i++) {
            senha.append(chars.charAt(random.nextInt(chars.length())));
        }

        return senha.toString();
    }

    private Perfil obterOuCriarPerfil(Perfil.TipoPerfil tipoPerfil) {
        return perfilRepository.findByNome(tipoPerfil)
                .orElseGet(() -> {
                    Perfil novoPerfil = new Perfil();
                    novoPerfil.setNome(tipoPerfil);
                    return perfilRepository.save(novoPerfil);
                });
    }

    // Método para forçar troca de senha no primeiro acesso
    @Transactional
    public void trocarSenha(String email, String senhaAtual, String novaSenha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setPrimeiroAcesso(false);
        usuarioRepository.save(usuario);
    }
}
