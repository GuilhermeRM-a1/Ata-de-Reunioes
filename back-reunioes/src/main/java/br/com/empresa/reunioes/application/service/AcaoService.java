package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.application.mapper.AcaoMapper;
import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.controller.dto.Acao.AcaoDTO;
import br.com.empresa.reunioes.web.controller.dto.Acao.AcaoRequest;
import br.com.empresa.reunioes.web.exception.RecursoNaoEncontradoException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Todo metodo publico devolve DTO — a camada web nao ve entidade JPA.
 */
@Service
@RequiredArgsConstructor
public class AcaoService {

    private final AcaoRepository repository;
    private final ColaboradorRepository colaboradorRepository;
    private final ReuniaoRepository reuniaoRepository;
    private final AcaoMapper mapper;

    @Transactional
    public Acao salvar(Long reuniaoId, AcaoRequest request) {

        Acao acao = mapper.toEntity(request);

        acao.setResponsavel(buscarResponsaveis(request.responsavel()));
        acao.setReuniao(buscarReuniao(reuniaoId));

        return repository.save(acao);
    }

    public Acao buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Ação", id));
    }

    // traz todas as acoes independente de reuniao, para fins de dashboard
    public List<Acao> listar() {
        return repository.findAll();
    }

    // traz acoes filtradas por reuniao, para fins de dashboard
    public List<Acao> listarPorReuniao(Long reuniaoId) {

        if (!reuniaoRepository.existsById(reuniaoId)) {
            throw RecursoNaoEncontradoException.de("Reunião", reuniaoId);
        } else {
            return repository.findAllByReuniaoId(reuniaoId);
        }

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

        Acao acao = buscarPorId(id);

        mapper.updateParsiEntity(acao, request);

        if (request.responsavel() != null) {
            acao.setResponsavel(buscarResponsaveis(request.responsavel()));
        }

        // nunca atualiza a reuniao

        return repository.save(acao);
    }

    @Transactional
    public void deletar(Long id) {

        Acao acao = buscarPorId(id);
        Reuniao reuniao = acao.getReuniao();
        reuniao.getAcoes().remove(acao);
        repository.delete(acao);
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
