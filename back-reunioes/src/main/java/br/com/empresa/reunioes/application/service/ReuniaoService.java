package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.application.mapper.ReuniaoMapper;
import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoRequest;
import br.com.empresa.reunioes.web.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


/**
 * Todo metodo publico devolve Entidade
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReuniaoService {

    private final ReuniaoRepository reuniaoRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final AcaoRepository acaoRepository;
    private final ReuniaoMapper mapper;

    @Transactional
    public Reuniao salvar(ReuniaoRequest request) {
        log.info("Iniciando salvamento da reunião.");

        log.debug("Requisição recebida: {}", request);

        Reuniao reuniao = mapper.toEntity(request);
        log.debug("requisição covertida para entidade");

        log.debug("Buscando e setando colaboradores.");
        reuniao.setParticipantes(buscarColaboradores(request.participantes()));

        log.debug("Buscando e setando ações.");
        reuniao.setAcoes(buscarAcoes(request.acoes()));

        log.debug("Calculando e setando o total de ações numericamente.");
        reuniao.setTotalAcoes(reuniao.getAcoes().size());

        log.debug("Reunião com colaboradores, ações e total de ações setados: {}", reuniao.getParticipantes(),
                reuniao.getAcoes(),
                reuniao.getTotalAcoes());

        Reuniao reuniaoSalva = reuniaoRepository.save(reuniao);

        log.info("Reunião salva com sucesso");

        return reuniaoSalva;
    }

    @Transactional(readOnly = true)
    public Reuniao buscarPorId(Long id) {
        log.info("Iniciando busca por reunião pelo id.");

        log.debug("Buscando reunião por id.");
        Reuniao reuniaoEncontrada = reuniaoRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Reunião", id));

        log.debug("Reunião encontrada: {}", reuniaoEncontrada);

        return reuniaoEncontrada;
    }

    @Transactional(readOnly = true)
    public Page<Reuniao> listar(Pageable paginacao) {
        log.info("Iniciando listagem de reuniões.");

        Page<Reuniao> listaPaginada = reuniaoRepository.findAll(paginacao);
        log.debug("Listagem de reuniões concluída. Total encontrado: {}", listaPaginada.getTotalElements());

        return listaPaginada;
    }

    @Transactional
    public Reuniao atualizar(Long id, ReuniaoRequest request) {

        Reuniao reuniao = buscarPorId(id);

        mapper.updateEntity(reuniao, request);

        reuniao.setParticipantes(buscarColaboradores(request.participantes()));
        reuniao.setAcoes(buscarAcoes(request.acoes()));
        reuniao.setTotalAcoes(reuniao.getAcoes().size());

        return this.reuniaoRepository.save(reuniao);
    }

    @Transactional
    public Reuniao atualizarParcial(Long id, ReuniaoRequest request) {
        log.info("Iniciando atualização parcial da reunião com id: {}", id);

        log.debug("Buscando reunião existente.");
        Reuniao reuniao = buscarPorId(id);

        log.debug("Atualizando com mapper");
        mapper.updateParsiEntity(reuniao, request);

        if (request.participantes() != null) {
            log.debug("buscando participantes");
            reuniao.setParticipantes(buscarColaboradores(request.participantes()));
        }


        if (request.acoes() != null) {
            log.debug("buscando ações");
            reuniao.setAcoes(buscarAcoes(request.acoes()));
            log.debug("calculando total de ações");
            reuniao.setTotalAcoes(reuniao.getAcoes().size());
        }

        log.debug("Salvando reunião atualizada.");
        Reuniao reuniaoAtualizada = reuniaoRepository.save(reuniao);

        log.info("Reunião atualizada salva com sucesso.");

        return reuniaoAtualizada;
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Iniciando processo de remover reunião");

        log.debug("Buscando reunião existente por id");
        Reuniao reuniao = buscarPorId(id);

        reuniaoRepository.delete(reuniao);
        log.info("Reunião removida com sucesso: ID: {}", id);
    }

    /**
     * Troca os ids da request pelas entidades do banco. findAllById descarta
     * id inexistente em silencio, entao a contagem e conferida para o cliente
     * receber 404 em vez de uma reuniao salva com participante faltando.
     */
    private List<Colaborador> buscarColaboradores(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<Colaborador> colaboradores = colaboradorRepository.findAllById(ids);

        if (colaboradores.size() != ids.stream().distinct().count()) {
            throw new RecursoNaoEncontradoException(
                    "Colaborador não encontrado entre os participantes informados");
        }

        return colaboradores;
    }

    private List<Acao> buscarAcoes(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<Acao> acoes = acaoRepository.findAllById(ids);

        if (acoes.size() != ids.stream().distinct().count()) {
            throw new RecursoNaoEncontradoException(
                    "Ação não encontrada entre as ações informadas");
        }

        return acoes;
    }
}












