package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.application.mapper.AcaoMapper;
import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.controller.dto.Acao.AcaoRequest;
import br.com.empresa.reunioes.web.exception.RecursoNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Todo metodo publico devolve Entidade
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AcaoService {

    private final AcaoRepository repository;
    private final ColaboradorRepository colaboradorRepository;
    private final ReuniaoRepository reuniaoRepository;
    private final AcaoMapper mapper;

    @Transactional
    public Acao salvar(Long reuniaoId, AcaoRequest request) {
        log.info("Iniciando processo de salvar AÇÃO");

        Acao acao = mapper.toEntity(request);
        log.debug("Requisição convertida para entidade.");

        log.debug("Buscando e setando responsáveis pela Ação.");
        acao.setResponsavel(buscarResponsaveis(request.responsavel()));

        log.debug("Buscando e setando reunião da ação.");
        acao.setReuniao(buscarReuniao(reuniaoId));

        log.debug("Salvando ação via repositório");
        Acao acaoSalva = repository.save(acao);
        log.info("Ação salva.");

        return acaoSalva;
    }

    @Transactional(readOnly = true)
    public Acao buscarPorId(Long id) {
        log.info("Iniciando busca por id da ação.");

        log.debug("Buscando ação por id");
        Acao acaoEncontrada = repository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Ação", id));

        log.debug("Ação encontrada: {}", acaoEncontrada);

        return acaoEncontrada;
    }

    @Transactional(readOnly = true)
    public Page<Acao> listar(Pageable paginacao) {
        log.info("Listando ações paginadas.");

        log.debug("Listando...");
        Page<Acao> pagina = repository.findAll(paginacao);

        log.debug("Lista gerada.");

        return pagina;
    }

    @Transactional(readOnly = true)
    public List<Acao> listarPorReuniao(Long reuniaoId) {
        log.info("Listando ações por reunião");

        List<Acao> lista = repository.findAllByReuniaoId(reuniaoId);

        if (!reuniaoRepository.existsById(reuniaoId)) {
            log.error("Reunião não encontrada ou não existe.");
            throw RecursoNaoEncontradoException.de("Reunião", reuniaoId);
        }

        log.debug("Lista gerada");

        return lista;
    }

    @Transactional
    public Acao atualizar(Long id, AcaoRequest request) {

        Acao acao = buscarPorId(id);

        mapper.updateEntity(acao, request);

        acao.setResponsavel(buscarResponsaveis(request.responsavel()));

        // nunca atualiza a reuniao

        return repository.save(acao);
    }

    @Transactional
    public Acao atualizarParcial(Long id, AcaoRequest request) {
        log.info("Atualizando Ação parcialmente");

        log.debug("Buscando ação por id.");
        Acao acao = buscarPorId(id);

        log.debug("Atualizando com mapper.");
        mapper.updateParsiEntity(acao, request);

        if (request.responsavel() != null) {
            log.debug("Buscando e setando colaboradores responsáveis pela ação.");
            acao.setResponsavel(buscarResponsaveis(request.responsavel()));
        }

        // nunca atualiza a reuniao
        log.debug("Chamando metodo save no repositorio");
        Acao acaoAtualizada = repository.save(acao);

        log.debug("Ação salva.");

        return acaoAtualizada;
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Iniciando processo de deletar ação.");

        log.debug("Buscando se ação existe por id.");
        Acao acao = buscarPorId(id);

        log.debug("Buscando se reunião existe(Para remover ação dela).");
        Reuniao reuniao = acao.getReuniao();

        log.debug("removendo ação da reunião.");
        reuniao.getAcoes().remove(acao);

        repository.delete(acao);
        log.debug("Ação deletada.");
    }


    private Reuniao buscarReuniao(Long id) {

        return reuniaoRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Reunião", id));
    }

    /**
     * Troca os ids da request pelas entidades do banco. findAllById descarta
     * id inexistente em silencio, entao a contagem e conferida para o cliente
     * receber 404 em vez de uma acao salva sem o responsavel pedido.
     */
    private List<Colaborador> buscarResponsaveis(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<Colaborador> colaboradores = colaboradorRepository.findAllById(ids);

        if (colaboradores.size() != ids.stream().distinct().count()) {
            throw new RecursoNaoEncontradoException(
                    "Colaborador não encontrado entre os responsáveis informados");
        }

        return colaboradores;
    }

}












