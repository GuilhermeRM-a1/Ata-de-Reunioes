package br.com.empresa.reunioes.application.reuniao;

import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.reuniao.dto.request.ReuniaoRequest;
import br.com.empresa.reunioes.domain.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static br.com.empresa.reunioes.application.reuniao.mapper.ReuniaoMapper.*;

/**
 * Todo metodo com retorn sempre retornará uma entidade
 * O mapper faz o trabalho de converter a request para entidade pronta para uso ao criar uma reuniao
 * Em alguns casos, metodos internos já fazem isso só pelo id por retornar um item existentes no banco
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ReuniaoService {

    private final ReuniaoRepository reuniaoRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final AcaoRepository acaoRepository;

    @Transactional
    public Reuniao salvar(ReuniaoRequest request) {
        log.info("Iniciando salvamento da reunião.");

        log.debug("Dados da reunião recebidos: título={}, data={}, participantes={}",
                request.titulo(),
                request.data(),
                request.participantes() != null ? request.participantes().size() : 0);

        // convertendo dto para entidade com ReuniaoMapper
        log.debug("Convertendo request para entity pronta para uso");
        Reuniao reuniao = reuniaoToEntity(request);

        log.debug("Buscando e setando colaboradores.");
        reuniao.setParticipantes(buscarColaboradores(request.participantes()));

        log.debug("Buscando e setando ações.");
        reuniao.setAcoes(buscarAcoes(request.acoes()));

        log.debug("Setando size de ações");
        reuniao.setTotalAcoes(reuniao.getAcoes().size());

        log.debug("Relacionamentos da reunião definidos: participantes={}, ações={}, totalAcoes={}",
                reuniao.getParticipantes().size(),
                reuniao.getAcoes().size(),
                reuniao.getTotalAcoes());


        // salvando
        Reuniao reuniaoSalva = reuniaoRepository.save(reuniao);

        log.info("Reunião salva com sucesso: id={}", reuniaoSalva.getId());

        return reuniaoSalva;
    }

    public Reuniao buscarPorId(Long id) {
        log.debug("Buscando reunião por id={}", id);

        Reuniao reuniaoEncontrada = reuniaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Reunião não encontrada."));

        log.debug("Reunião encontrada: id={}", id);

        return reuniaoEncontrada;
    }

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

        // TRAZENDO REUNIÃO
        Reuniao reuniao = buscarPorId(id);

        // ATUALIZANDO
        log.debug("Atualizando reunião: id={}", id);
        reuniao.setTitulo(request.titulo());
        reuniao.setData(request.data());
        reuniao.setStatusTranscricao(request.statusTranscricao());
        reuniao.setStatusReuniao(request.statusReuniao());
        reuniao.setAreas(request.areas());
        reuniao.setPontosChaves(request.pontosChaves());
        reuniao.setResumo(request.resumo());
        reuniao.setParticipantes(buscarColaboradores(request.participantes()));
        substituirAcoes(reuniao, buscarAcoes(request.acoes()));
        reuniao.setTotalAcoes(reuniao.getAcoes().size());

        // SALVANDO
        Reuniao reuniaoAtualizada = reuniaoRepository.save(reuniao);

        log.info("Reunião atualizada com sucesso: id={}", id);

        return reuniaoAtualizada;
    }

    @Transactional
    public Reuniao atualizarParcial(Long id, ReuniaoRequest request) {
        log.info("Iniciando atualização parcial da reunião com id={}", id);

        // TRAZENDO REUNIÃO
        Reuniao reuniao = buscarPorId(id);

        // ATUALIZANDO
        log.debug("Atualizando reunião parcialmente: id={}", id);

        if (request.titulo() != null) {
            log.debug("Atualizando título");
            reuniao.setTitulo(request.titulo());
        }

        if (request.data() != null) {
            log.debug("Atualizando data");
            reuniao.setData(request.data());
        }

        if (request.statusTranscricao() != null) {
            log.debug("Atualizando status da transcrição");
            reuniao.setStatusTranscricao(request.statusTranscricao());
        }

        if (request.statusReuniao() != null) {
            log.debug("Atualizando status da reunião");
            reuniao.setStatusReuniao(request.statusReuniao());
        }

        if (request.areas() != null) {
            log.debug("Atualizando areas da reunião");
            reuniao.setAreas(request.areas());
        }

        if (request.pontosChaves() != null) {
            log.debug("Atualizando pontos chaves da reunião");
            reuniao.setPontosChaves(request.pontosChaves());
        }

        if (request.resumo() != null) {
            log.debug("Atualizando resumo da atualização");
            reuniao.setResumo(request.resumo());
        }

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

        // SALVANDO
        Reuniao reuniaoAtualizada = reuniaoRepository.save(reuniao);

        log.info("Reunião atualizada parcialmente com sucesso: id={}", id);

        return reuniaoAtualizada;
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Iniciando processo de remoção da reunião: id={}", id);

        // trazendo reuniao
        Reuniao reuniao = buscarPorId(id);

        // deletando
        reuniaoRepository.delete(reuniao);

        log.info("Reunião removida com sucesso: id={}", id);
    }

    /** -------------= METODOS AUXILIARES =------------- */

    /**
     * Acoes usa orphanRemoval, e o Hibernate recusa que a colecao seja trocada
     * por outra instancia — quem faz isso leva "a collection with orphan
     * deletion was no longer referenced". Entao a lista existente e alterada
     * no lugar, e nao substituida.
     * <p>
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