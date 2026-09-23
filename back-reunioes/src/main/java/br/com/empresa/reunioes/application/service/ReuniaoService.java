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

import java.util.ArrayList;
import java.util.List;

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

        log.debug("Dados da reunião recebidos: título={}, data={}, participantes={}",
                request.titulo(),
                request.data(),
                request.participantes() != null ? request.participantes().size() : 0);

        Reuniao reuniao = mapper.toEntity(request);
        log.debug("Requisição convertida para entidade.");

        log.debug("Buscando e setando colaboradores.");
        reuniao.setParticipantes(buscarColaboradores(request.participantes()));

        log.debug("Buscando e setando ações.");
        substituirAcoes(reuniao, buscarAcoes(request.acoes()));

        reuniao.setTotalAcoes(reuniao.getAcoes().size());

        log.debug("Relacionamentos da reunião definidos: participantes={}, ações={}, totalAcoes={}",
                reuniao.getParticipantes().size(),
                reuniao.getAcoes().size(),
                reuniao.getTotalAcoes());

        Reuniao reuniaoSalva = reuniaoRepository.save(reuniao);

        log.info("Reunião salva com sucesso: id={}", reuniaoSalva.getId());

        return reuniaoSalva;
    }

    @Transactional(readOnly = true)
    public Reuniao buscarPorId(Long id) {
        log.debug("Buscando reunião por id={}", id);

        Reuniao reuniaoEncontrada = reuniaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Reunião não encontrada."));

        log.debug("Reunião encontrada: id={}", id);

        return reuniaoEncontrada;
    }

    @Transactional(readOnly = true)
    public Page<Reuniao> listar(Pageable paginacao) {
        log.info("Iniciando listagem de reuniões.");

        Page<Reuniao> listaPaginada = reuniaoRepository.findAll(paginacao);

        log.debug("Listagem de reuniões concluída. Total encontrado: {}",
                listaPaginada.getTotalElements());

        return listaPaginada;
    }

    @Transactional
    public Reuniao atualizar(Long id, ReuniaoRequest request) {
        log.info("Iniciando atualização da reunião com id={}", id);

        Reuniao reuniao = buscarPorId(id);

        mapper.updateEntity(reuniao, request);

        reuniao.setParticipantes(buscarColaboradores(request.participantes()));
        substituirAcoes(reuniao, buscarAcoes(request.acoes()));
        reuniao.setTotalAcoes(reuniao.getAcoes().size());

        Reuniao reuniaoAtualizada = reuniaoRepository.save(reuniao);

        log.info("Reunião atualizada com sucesso: id={}", id);

        return reuniaoAtualizada;
    }

    @Transactional
    public Reuniao atualizarParcial(Long id, ReuniaoRequest request) {
        log.info("Iniciando atualização parcial da reunião com id={}", id);

        Reuniao reuniao = buscarPorId(id);

        log.debug("Atualizando reunião parcialmente com mapper: id={}", id);
        mapper.updateParcialEntity(reuniao, request);

        if (request.participantes() != null) {
            log.debug("Atualizando participantes da reunião: id={}", id);
            reuniao.setParticipantes(buscarColaboradores(request.participantes()));
        }

        if (request.acoes() != null) {
            log.debug("Atualizando ações da reunião: id={}", id);
            substituirAcoes(reuniao, buscarAcoes(request.acoes()));

            log.debug("Recalculando total de ações da reunião: id={}", id);
            reuniao.setTotalAcoes(reuniao.getAcoes().size());
        }

        Reuniao reuniaoAtualizada = reuniaoRepository.save(reuniao);

        log.info("Reunião atualizada parcialmente com sucesso: id={}", id);

        return reuniaoAtualizada;
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Iniciando processo de remoção da reunião: id={}", id);

        Reuniao reuniao = buscarPorId(id);

        reuniaoRepository.delete(reuniao);

        log.info("Reunião removida com sucesso: id={}", id);
    }


    /**
     * Acoes usa orphanRemoval, e o Hibernate recusa que a colecao seja trocada
     * por outra instancia — quem faz isso leva "a collection with orphan
     * deletion was no longer referenced". Entao a lista existente e alterada
     * no lugar, e nao substituida.
     *
     * Atencao ao efeito: acao que nao vier na lista deixa de pertencer a
     * reuniao e o orphanRemoval a apaga. E a semantica do PUT, que substitui
     * o recurso inteiro.
     */
    private void substituirAcoes(Reuniao reuniao, List<Acao> novas) {

        if (reuniao.getAcoes() == null) {
            reuniao.setAcoes(new ArrayList<>());
        }

        reuniao.getAcoes().clear();

        for (Acao acao : novas) {
            acao.setReuniao(reuniao);
            reuniao.getAcoes().add(acao);
        }
    }
    private List<Colaborador> buscarColaboradores(List<Long> ids) {

        // ArrayList e nao List.of(): a colecao vai para uma entidade gerenciada
        // e o Hibernate precisa poder altera-la. Lista imutavel aqui derruba o
        // save com UnsupportedOperationException.
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<Colaborador> colaboradores = colaboradorRepository.findAllById(ids);

        if (colaboradores.size() != ids.stream().distinct().count()) {
            throw new RecursoNaoEncontradoException(
                    "Colaborador não encontrado entre os participantes informados.");
        }

        return colaboradores;
    }

    private List<Acao> buscarAcoes(List<Long> ids) {

        // Mesmo motivo de buscarColaboradores: a lista precisa ser mutavel.
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<Acao> acoes = acaoRepository.findAllById(ids);

        if (acoes.size() != ids.stream().distinct().count()) {
            throw new RecursoNaoEncontradoException(
                    "Ação não encontrada entre as ações informadas.");
        }

        return acoes;
    }
}