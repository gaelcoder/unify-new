package com.projeto.unify.services;

import com.projeto.unify.dtos.UniversidadeDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.repositories.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.projeto.unify.dtos.UniversidadeStatsDTO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UniversidadeService {

    private static final Logger logger = LoggerFactory.getLogger(UniversidadeService.class);

    private final UniversidadeRepository universidadeRepository;
    private final RepresentanteRepository representanteRepository;
    private final RepresentanteService representanteService;
    private final UsuarioService usuarioService;
    private final FileService fileService;
    private final FuncionarioRepository funcionarioRepository;
    private final AlunoRepository alunoRepository;

    @Transactional
    public Universidade criar(UniversidadeDTO dto, MultipartFile logoFile) {
        logger.info("Iniciando criação de universidade: {}", dto.getNome());

        if (universidadeRepository.existsByCnpj(dto.getCnpj())) {
            logger.warn("CNPJ já cadastrado: {}", dto.getCnpj());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNPJ já cadastrado");
        }

        Representante representante = null;
        if (dto.getRepresentanteId() != null) {
            logger.info("Buscando representante com ID: {}", dto.getRepresentanteId());
            representante = representanteRepository.findById(dto.getRepresentanteId())
                    .orElseThrow(() -> {
                        logger.warn("Representante não encontrado com ID: {}", dto.getRepresentanteId());
                        return new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Representante não encontrado");
                    });

            if (representante.getUniversidade() != null) {
                logger.warn("Representante {} já associado à universidade: {}",
                        representante.getId(), representante.getUniversidade().getNome());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Representante já está associado a outra universidade");
            }
            logger.info("Representante encontrado: {} {}", representante.getNome(), representante.getSobrenome());
        }

        String logoPath = null;
        if (logoFile != null && !logoFile.isEmpty()) {
            logoPath = fileService.store(logoFile);
        }

        Universidade universidade = new Universidade(
                dto.getNome(),
                dto.getCnpj(),
                dto.getFundacao(),
                dto.getSigla(),
                logoPath,
                null,
                dto.getCampus()
        );

        logger.info("Salvando universidade no banco de dados (primeira vez)");
        universidade = universidadeRepository.save(universidade);
        logger.info("Universidade salva com ID: {}", universidade.getId());

        if (representante != null) {
            try {
                logger.info("Associando representante ID: {} à universidade recém-criada ID: {}", representante.getId(), universidade.getId());

                representante.setUniversidade(universidade);
                universidade.setRepresentante(representante);

                Representante managedRepresentante = this.representanteService.associarRepresentante(
                    universidade, 
                    representante, 
                    representante.getEmail()
                );

                universidade.setRepresentante(managedRepresentante);

                universidade = salvarUniversidadeTransactional(universidade);

                logger.info("Representante ID: {} associado com sucesso à universidade ID: {} e credenciais criadas.", 
                            managedRepresentante.getId(), universidade.getId());

            } catch (Exception e) {
                logger.error("Erro ao associar representante ID {} à universidade {}: {}", 
                             representante.getId(), universidade.getNome(), e.getMessage(), e);

                 throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erro ao associar representante e criar usuário: " + e.getMessage(), e);
            }
        }

        return universidade;
    }

    public List<Universidade> listarTodos() {
        logger.debug("Listando todas as universidades com representantes");
        return universidadeRepository.findAllWithRepresentante();
    }

    public Universidade buscarPorId(Long id) {
        logger.debug("Buscando universidade com ID: {}", id);
        Universidade universidade = universidadeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Universidade não encontrada com ID: {}", id);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Universidade não encontrada");
                });

        if (universidade.getCampus() != null) {
            universidade.getCampus().size();
        }

        if (universidade.getRepresentante() != null) {
            universidade.getRepresentante().getNome();
        }
        return universidade;
    }

    public Universidade atualizar(Long id, UniversidadeDTO dto, MultipartFile logoFile) {
        logger.info("Atualizando universidade com ID: {}", id);
        Universidade universidade = buscarPorId(id);

        universidade.setNome(dto.getNome());
        // Não atualizar CNPJ
        universidade.setFundacao(dto.getFundacao());
        universidade.setSigla(dto.getSigla());
        universidade.setCampus(dto.getCampus());

        if (logoFile != null && !logoFile.isEmpty()) {
            if (universidade.getLogoPath() != null && !universidade.getLogoPath().isEmpty()) {
                try {
                    fileService.delete(universidade.getLogoPath());
                    logger.info("Logo antigo deletado: {}", universidade.getLogoPath());
                } catch (Exception e) {
                    logger.warn("Falha ao deletar logo antigo: {}. Error: {}", universidade.getLogoPath(), e.getMessage());
                }
            }
            String novoLogoPath = fileService.store(logoFile);
            universidade.setLogoPath(novoLogoPath);
            logger.info("Novo logo salvo em: {}", novoLogoPath);
        }

        logger.info("Salvando universidade atualizada");
        return universidadeRepository.save(universidade);
    }

    public Universidade associarRepresentante(Long universidadeId, Long representanteId) {
        logger.info("Iniciando associação do representante ID: {} à universidade ID: {}",
                representanteId, universidadeId);

        Universidade universidade = buscarPorId(universidadeId);
        logger.info("Universidade encontrada: {}", universidade.getNome());

        // Verificar se já tem representante
        if (universidade.getRepresentante() != null) {
            logger.warn("Universidade já tem um representante associado: {}",
                    universidade.getRepresentante().getNome());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Esta universidade já possui um representante associado");
        }

        Representante representante = representanteService.buscarPorId(representanteId);
        logger.info("Representante encontrado: {} {}, Email: {}",
                representante.getNome(), representante.getSobrenome(), representante.getEmail());

        // Verificar se o representante já está associado a outra universidade
        if (representante.getUniversidade() != null) {
            logger.warn("Representante já está associado à universidade: {}",
                    representante.getUniversidade().getNome());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Representante já está associado a outra universidade");
        }

        try {
            logger.info("Associando representante");
            representante.setUniversidade(universidade);
            universidade.setRepresentante(representante);

            logger.info("Calling RepresentanteService to associate and save representative details");
            Representante managedRepresentante = this.representanteService.associarRepresentante(universidade, representante, representante.getEmail());


            universidade.setRepresentante(managedRepresentante);
            Universidade savedUniversidade = salvarUniversidadeTransactional(universidade);

            logger.info("Representante associado com sucesso e credenciais criadas");
            return savedUniversidade;
        } catch (Exception e) {
            logger.error("Erro ao associar representante: {}", e.getMessage(), e);

            if (e.getMessage() != null && e.getMessage().contains("Já existe um administrador")) {
                logger.warn("Já existe um administrador para esta universidade, mas a associação foi realizada");
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erro ao associar representante: " + e.getMessage());
            }

            if (e instanceof ResponseStatusException) {
                throw e;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erro ao associar representante: " + e.getMessage());
            }
        }
    }

    @Transactional
    public Universidade salvarUniversidadeTransactional(Universidade universidade) {
        logger.info("Salvando universidade transactionalmente: {}", universidade.getNome());
        return universidadeRepository.save(universidade);
    }

    @Transactional
    public Universidade desassociarRepresentante(Long universidadeId) {
        logger.info("Desassociando representante da universidade ID: {}", universidadeId);
        Universidade universidade = buscarPorId(universidadeId);

        if (universidade.getRepresentante() != null) {
            Representante representante = universidade.getRepresentante();
            logger.info("Removendo associação com representante: {} {}",
                    representante.getNome(), representante.getSobrenome());

            Usuario usuarioDoRepresentante = representante.getUsuario();

            // Desassociar ambos os lados
            representante.setUniversidade(null);
            universidade.setRepresentante(null);

            // Salvar as entidades
            representanteRepository.save(representante);
            universidade = universidadeRepository.save(universidade);

            // Desativar o usuário associado ao representante
            if (usuarioDoRepresentante != null) {
                logger.info("Desativando usuário: {}", usuarioDoRepresentante.getEmail());
                usuarioDoRepresentante.setAtivo(false);
                usuarioService.save(usuarioDoRepresentante);
                logger.info("Usuário {} desativado com sucesso.", usuarioDoRepresentante.getEmail());
            } else {
                logger.warn("Representante {} não possui um usuário associado para desativar.", representante.getNome());
            }

            logger.info("Representante desassociado com sucesso");

        } else {
            logger.info("Universidade não possui representante associado");
        }

        return universidade;
    }

    @Transactional
    public void excluir(Long id) {
        logger.info("Excluindo universidade com ID: {}", id);
        Universidade universidade = buscarPorId(id);

        if (universidade.getLogoPath() != null && !universidade.getLogoPath().isEmpty()) {
            fileService.delete(universidade.getLogoPath());
            logger.info("Logo da universidade {} excluído: {}", universidade.getNome(), universidade.getLogoPath());
        }
        
        // Se tiver um representante associado, desassociar primeiro e desativar usuário
        if (universidade.getRepresentante() != null) {
            Representante representante = universidade.getRepresentante();
            logger.info("Desassociando e desativando usuário do representante: {} {} da universidade {}", 
                        representante.getNome(), representante.getSobrenome(), universidade.getNome());
            
            // Desassociar o representante da universidade. 
            // O método desassociarRepresentante em RepresentanteService cuida da lógica de desativação do usuário.
            this.representanteService.desassociarRepresentante(representante);
            universidade.setRepresentante(null);
            universidadeRepository.save(universidade); 
        }

        logger.info("Removendo universidade do banco de dados: {}", universidade.getNome());
        universidadeRepository.delete(universidade);
        logger.info("Universidade {} excluída com sucesso", universidade.getNome());
    }

    @Transactional(readOnly = true)
    public UniversidadeStatsDTO getStatsMinhaUniversidade() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.findByEmail(emailUsuarioLogado);

        Representante representante = representanteRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não é um representante válido."));
        
        Universidade universidade = representante.getUniversidade();
        if (universidade == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Representante não associado a nenhuma universidade.");
        }

        Universidade universidadeCompleta = universidadeRepository.findById(universidade.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Universidade não encontrada."));

        int campusCount = universidadeCompleta.getCampus() != null ? universidadeCompleta.getCampus().size() : 0;
        long funcionariosCount = funcionarioRepository.countByUniversidadeId(universidadeCompleta.getId());
        long alunosCount = alunoRepository.countByUniversidadeId(universidadeCompleta.getId());

        return new UniversidadeStatsDTO(
            universidadeCompleta.getNome(),
            universidadeCompleta.getId(),
            campusCount,
            funcionariosCount,
            alunosCount
        );
    }

    @Transactional(readOnly = true)
    public long countTotalUniversidades() {
        return universidadeRepository.count();
    }

    @Transactional(readOnly = true)
    public Universidade findMyUniversity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(email);

        return usuario.getPerfis().stream()
                .findFirst()
                .flatMap(perfil -> {
                    switch (perfil.getNome()) {
                        case ROLE_FUNCIONARIO:
                        case ROLE_FUNCIONARIO_RH:
                            return funcionarioRepository.findByUsuarioId(usuario.getId())
                                    .map(Funcionario::getUniversidade);
                        case ROLE_ALUNO:
                            return alunoRepository.findByUsuario(usuario)
                                    .map(Aluno::getUniversidade);
                        case ROLE_PROFESSOR:
                            // Professor logic to be implemented
                            return Optional.empty();
                        case ROLE_ADMIN_UNIVERSIDADE:
                            return representanteRepository.findByUsuarioId(usuario.getId())
                                    .map(Representante::getUniversidade);
                        default:
                            return Optional.empty();
                    }
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Universidade não encontrada para o usuário ou perfil não mapeado."));
    }

    public List<String> getCampi(Long id) {
        Universidade universidade = buscarPorId(id);
        return universidade.getCampus();
    }
}