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
                null, // Representante initially null, will be set if provided
                dto.getCampus()
        );

        logger.info("Salvando universidade no banco de dados (primeira vez)");
        universidade = universidadeRepository.save(universidade);
        logger.info("Universidade salva com ID: {}", universidade.getId());

        if (representante != null) {
            try {
                logger.info("Associando representante ID: {} à universidade recém-criada ID: {}", representante.getId(), universidade.getId());

                representante.setUniversidade(universidade);
                universidade.setRepresentante(representante); // Set in-memory before calling service

                // Call RepresentanteService.associarRepresentante
                // This service method handles user creation, email, and saving the Representante.
                Representante managedRepresentante = this.representanteService.associarRepresentante(
                    universidade, 
                    representante, 
                    representante.getEmail()
                );

                // Ensure the universidade object has the managed representative from the service call
                universidade.setRepresentante(managedRepresentante);
                
                // Save the universidade again using a method with REQUIRES_NEW propagation
                // to ensure the association is persisted in its own transaction.
                universidade = salvarUniversidadeTransactional(universidade);

                logger.info("Representante ID: {} associado com sucesso à universidade ID: {} e credenciais criadas.", 
                            managedRepresentante.getId(), universidade.getId());

            } catch (Exception e) {
                logger.error("Erro ao associar representante ID {} à universidade {}: {}", 
                             representante.getId(), universidade.getNome(), e.getMessage(), e);
                // Consider re-throwing to ensure consistency if representative association is critical
                 throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erro ao associar representante e criar usuário: " + e.getMessage(), e);
            }
        }

        return universidade;
    }

    public List<Universidade> listarTodos() {
        logger.debug("Listando todas as universidades");
        return universidadeRepository.findAll();
    }

    public Universidade buscarPorId(Long id) {
        logger.debug("Buscando universidade com ID: {}", id);
        return universidadeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Universidade não encontrada com ID: {}", id);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Universidade não encontrada");
                });
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
            // Delete old logo if it exists
            if (universidade.getLogoPath() != null && !universidade.getLogoPath().isEmpty()) {
                try {
                    fileService.delete(universidade.getLogoPath());
                    logger.info("Logo antigo deletado: {}", universidade.getLogoPath());
                } catch (Exception e) {
                    logger.warn("Falha ao deletar logo antigo: {}. Error: {}", universidade.getLogoPath(), e.getMessage());
                    // Continue even if old logo deletion fails for some reason
                }
            }
            // Store new logo
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
            // Set relationships in memory first - these are not persisted yet
            logger.info("Setting representative on university and vice-versa in memory");
            representante.setUniversidade(universidade);
            universidade.setRepresentante(representante);

            // Call RepService. This runs in a new transaction and saves the Representante
            // and its associated Usuario.
            logger.info("Calling RepresentanteService to associate and save representative details");
            Representante managedRepresentante = this.representanteService.associarRepresentante(universidade, representante, representante.getEmail());

            // Now, save the universidade with its new representative in a separate transaction.
            universidade.setRepresentante(managedRepresentante); // Ensure we use the returned, managed rep
            Universidade savedUniversidade = salvarUniversidadeTransactional(universidade);

            logger.info("Representante associado com sucesso e credenciais criadas");
            return savedUniversidade;
        } catch (Exception e) {
            logger.error("Erro ao associar representante: {}", e.getMessage(), e);

            if (e.getMessage() != null && e.getMessage().contains("Já existe um administrador")) {
                logger.warn("Já existe um administrador para esta universidade, mas a associação foi realizada");
                // Decide if you still want to return universidade or throw
                // For now, let's re-throw to be consistent with other errors
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erro ao associar representante: " + e.getMessage());
            }

            // Ensure a ResponseStatusException is thrown for other cases from this try-catch
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

            Usuario usuarioDoRepresentante = representante.getUsuario(); // Get the Usuario

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
                usuarioService.save(usuarioDoRepresentante); // Save using UsuarioService
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

        // Delete logo if it exists
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
            universidade.setRepresentante(null); // Garantir que a referência é removida na entidade universidade
            // Salvar a universidade após remover o representante para persistir a mudança
            // Isso é importante se a exclusão da universidade não for cascatear essa mudança imediatamente
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

        // Fetch the full university entity again to ensure all collections are available if needed
        // and to work with the most up-to-date data.
        Universidade universidadeCompleta = universidadeRepository.findById(universidade.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Universidade não encontrada."));

        int campusCount = universidadeCompleta.getCampus() != null ? universidadeCompleta.getCampus().size() : 0;
        long funcionariosCount = funcionarioRepository.countByUniversidadeId(universidadeCompleta.getId());
        long alunosCount = 0; // Placeholder - Implement when Aluno entity and repository are available
        // alunosCount = alunoRepository.countByUniversidadeId(universidadeCompleta.getId());

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
}