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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Todo método público devolve Entidade.
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
        log.info("Iniciando processo de salvar ação: reuniaoId={}", reuniaoId);

        Acao acao = mapper.toEntity(request);
        log.debug("Requisição convertida para entidade.");

        log.debug("Buscando e setando responsáveis pela ação.");
        acao.setResponsavel(buscarResponsaveis(request.responsavel()));

        log.debug("Buscando e setando reunião da ação: reuniaoId={}", reuniaoId);
        acao.setReuniao(buscarReuniao(reuniaoId));

        log.debug("Salvando ação via repositório.");
        Acao acaoSalva = repository.save(acao);

        log.info("Ação salva com sucesso: id={}, reuniaoId={}",
                acaoSalva.getId(), reuniaoId);

        return acaoSalva;
    }

    @Transactional(readOnly = true)
    public Acao buscarPorId(Long id) {
        log.debug("Buscando ação por id={}", id);

        Acao acaoEncontrada = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Ação não encontrada"));

        log.debug("Ação encontrada: id={}", id);

        return acaoEncontrada;
    }

    @Transactional(readOnly = true)
    public Page<Acao> listar(Pageable paginacao) {
        log.info("Iniciando listagem paginada de ações.");

        Page<Acao> pagina = repository.findAll(paginacao);

        log.debug("Listagem de ações concluída. Total encontrado: {}",
                pagina.getTotalElements());

        return pagina;
    }

    @Transactional(readOnly = true)
    public List<Acao> listarPorReuniao(Long reuniaoId) {
        log.info("Iniciando listagem de ações da reunião: reuniaoId={}", reuniaoId);

        if (!reuniaoRepository.existsById(reuniaoId)) {
            log.warn("Reunião não encontrada: id={}", reuniaoId);
            throw new RecursoNaoEncontradoException(
                    "Reunião não encontrada.");
        }

        List<Acao> lista = repository.findAllByReuniaoId(reuniaoId);

        log.debug(
                "Listagem de ações da reunião concluída: reuniaoId={}, total={}",
                reuniaoId,
                lista.size()
        );

        return lista;
    }

    @Transactional
    public Acao atualizar(Long id, AcaoRequest request) {
        log.info("Iniciando atualização da ação: id={}", id);

        Acao acao = buscarPorId(id);

        log.debug("Atualizando ação com mapper: id={}", id);
        mapper.updateEntity(acao, request);

        log.debug("Atualizando responsáveis da ação: id={}", id);
        acao.setResponsavel(buscarResponsaveis(request.responsavel()));

        // Nunca atualiza a reunião.

        Acao acaoAtualizada = repository.save(acao);

        log.info("Ação atualizada com sucesso: id={}", id);

        return acaoAtualizada;
    }

    @Transactional
    public Acao atualizarParcial(Long id, AcaoRequest request) {
        log.info("Iniciando atualização parcial da ação: id={}", id);

        Acao acao = buscarPorId(id);

        log.debug("Atualizando ação parcialmente com mapper: id={}", id);
        mapper.updateParcialEntity(acao, request);

        if (request.responsavel() != null) {
            log.debug("Atualizando responsáveis da ação: id={}", id);
            acao.setResponsavel(buscarResponsaveis(request.responsavel()));
        }

        // Nunca atualiza a reunião.

        Acao acaoAtualizada = repository.save(acao);

        log.info("Ação atualizada parcialmente com sucesso: id={}", id);

        return acaoAtualizada;
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Iniciando processo de remoção da ação: id={}", id);

        Acao acao = buscarPorId(id);

        log.debug("Buscando reunião associada à ação: acaoId={}", id);
        Reuniao reuniao = acao.getReuniao();

        log.debug("Removendo ação da reunião: acaoId={}, reuniaoId={}",
                id, reuniao.getId());

        reuniao.getAcoes().remove(acao);

        repository.delete(acao);

        log.info("Ação removida com sucesso: id={}", id);
    }

    private Reuniao buscarReuniao(Long id) {
        return reuniaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Reunião não encontrada"));
    }

    /**
     * Troca os IDs da request pelas entidades do banco.
     * findAllById ignora IDs inexistentes, então a contagem é conferida
     * para que o cliente receba 404 em vez de uma ação ser salva
     * sem o responsável solicitado.
     */
    private List<Colaborador> buscarResponsaveis(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<Colaborador> colaboradores =
                colaboradorRepository.findAllById(ids);

        if (colaboradores.size() != ids.stream().distinct().count()) {
            throw new RecursoNaoEncontradoException(
                    "Colaborador não encontrado entre os responsáveis informados");
        }

        return colaboradores;
    }
}