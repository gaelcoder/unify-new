package com.projeto.unify.services;

import com.projeto.unify.dtos.UniversidadeDTO;
import com.projeto.unify.models.*;
import com.projeto.unify.repositories.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UniversidadeService {

    private static final Logger logger = LoggerFactory.getLogger(UniversidadeService.class);

    private final UniversidadeRepository universidadeRepository;
    private final RepresentanteRepository representanteRepository;
    private final RepresentanteService representanteService;
    private final UsuarioService usuarioService;

    @Transactional
    public Universidade criar(UniversidadeDTO dto) {
        logger.info("Iniciando criação de universidade: {}", dto.getNome());

        // Verificar se CNPJ já existe
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

            // Verificar se representante já está associado a outra universidade
            if (representante.getUniversidade() != null) {
                logger.warn("Representante já associado a outra universidade: {}",
                        representante.getUniversidade().getNome());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Representante já está associado a outra universidade");
            }

            logger.info("Representante encontrado: {} {}", representante.getNome(), representante.getSobrenome());
        }

        // Criar universidade sem associar representante inicialmente
        Universidade universidade = new Universidade(
                dto.getNome(),
                dto.getCnpj(),
                dto.getFundacao(),
                dto.getSigla(),
                null, // Não associar representante ainda
                dto.getCampus()
        );

        logger.info("Salvando universidade no banco de dados");
        universidade = universidadeRepository.save(universidade);
        logger.info("Universidade salva com ID: {}", universidade.getId());

        // Se tiver representante, associar e criar credenciais administrativas
        if (representante != null) {
            try {
                logger.info("Associando representante ID: {} à universidade recém-criada", representante.getId());
                // Associar manualmente
                representante.setUniversidade(universidade);
                universidade.setRepresentante(representante);

                // Salvar ambas as entidades
                representanteRepository.save(representante);
                universidade = universidadeRepository.save(universidade);

                // Criar usuário admin para o representante
                logger.info("Criando usuário administrador para o representante");
                representanteService.associarRepresentante(universidade, representante, representante.getEmail());

                logger.info("Representante associado com sucesso e credenciais criadas");
            } catch (Exception e) {
                logger.error("Erro ao associar representante à universidade: {}", e.getMessage(), e);
                // Não vamos lançar a exceção para manter a consistência do banco
                // A universidade foi criada, mas o representante não foi associado
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

    public Universidade atualizar(Long id, UniversidadeDTO dto) {
        logger.info("Atualizando universidade com ID: {}", id);
        Universidade universidade = buscarPorId(id);

        universidade.setNome(dto.getNome());
        // Não atualizar CNPJ
        universidade.setFundacao(dto.getFundacao());
        universidade.setSigla(dto.getSigla());
        universidade.setCampus(dto.getCampus());

        logger.info("Salvando universidade atualizada");
        return universidadeRepository.save(universidade);
    }

    @Transactional
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
            // Associar representante à universidade
            logger.info("Associando representante à universidade");
            representante.setUniversidade(universidade);
            universidade.setRepresentante(representante);

            // Salvar as entidades
            representanteRepository.save(representante);
            universidade = universidadeRepository.save(universidade);

            // Criar usuário admin para o representante
            logger.info("Criando usuário administrador para o representante");
            representanteService.associarRepresentante(universidade, representante, representante.getEmail());

            logger.info("Representante associado com sucesso e credenciais criadas");
            return universidade;
        } catch (Exception e) {
            logger.error("Erro ao associar representante: {}", e.getMessage(), e);

            // Se o erro for porque já existe um admin, registre mas não interrompa
            if (e.getMessage() != null && e.getMessage().contains("Já existe um administrador")) {
                logger.warn("Já existe um administrador para esta universidade, mas a associação foi realizada");
                return universidade;
            }

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao associar representante: " + e.getMessage());
        }
    }

    @Transactional
    public Universidade desassociarRepresentante(Long universidadeId) {
        logger.info("Desassociando representante da universidade ID: {}", universidadeId);
        Universidade universidade = buscarPorId(universidadeId);

        if (universidade.getRepresentante() != null) {
            Representante representante = universidade.getRepresentante();
            logger.info("Removendo associação com representante: {} {}",
                    representante.getNome(), representante.getSobrenome());

            // Desassociar ambos os lados
            representante.setUniversidade(null);
            universidade.setRepresentante(null);

            // Salvar as entidades
            representanteRepository.save(representante);
            universidade = universidadeRepository.save(universidade);

            logger.info("Representante desassociado com sucesso");

            // Aqui você poderia ter uma lógica para desativar o usuário admin
            // associado a este representante
        } else {
            logger.info("Universidade não possui representante associado");
        }

        return universidade;
    }

    @Transactional
    public void excluir(Long id) {
        logger.info("Excluindo universidade com ID: {}", id);
        Universidade universidade = buscarPorId(id);

        // Se tiver um representante associado, desassociar primeiro
        if (universidade.getRepresentante() != null) {
            logger.info("Desassociando representante antes da exclusão");
            Representante representante = universidade.getRepresentante();
            representante.setUniversidade(null);
            universidade.setRepresentante(null);
            representanteRepository.save(representante);
        }

        logger.info("Removendo universidade do banco de dados");
        universidadeRepository.delete(universidade);
        logger.info("Universidade excluída com sucesso");
    }
}