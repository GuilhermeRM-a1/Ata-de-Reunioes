package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.controller.dto.PaginaResponse;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoDTO;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoRequest;
import br.com.empresa.reunioes.web.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * A camada web nunca recebe entidade JPA: todo metodo publico devolve DTO.
 * Isso evita vazar campo interno na resposta e serializacao de relacionamento
 * preguicoso fora da transacao.
 */
@Service
@RequiredArgsConstructor
public class ReuniaoService{

    private final ReuniaoRepository reuniaoRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final AcaoRepository acaoRepository;

    public ReuniaoDTO salvar(ReuniaoRequest request) {
        Reuniao reuniao = new Reuniao();

        reuniao.setTitulo(request.titulo());
        reuniao.setData(request.data());
        reuniao.setStatus(request.status());
        reuniao.setAreas(request.areas());
        reuniao.setPontosChaves(request.pontosChaves());

        reuniao.setParticipantes(buscarColaboradores(request.participantes()));
        reuniao.setAcoes(buscarAcoes(request.acoes()));
        reuniao.setTotalAcoes(calcularTotalAcoes(request, reuniao.getAcoes()));

        return ReuniaoDTO.de(reuniaoRepository.save(reuniao));
    }

    public ReuniaoDTO buscarPorId(Long id) {
        return ReuniaoDTO.de(buscarEntidade(id));
    }

    public List<ReuniaoDTO> listar() {
        return reuniaoRepository.findAll().stream()
                .map(ReuniaoDTO::de)
                .toList();
    }

    public PaginaResponse<ReuniaoDTO> listar(Pageable paginacao) {
        return PaginaResponse.de(reuniaoRepository.findAll(paginacao).map(ReuniaoDTO::de));
    }

    public ReuniaoDTO atualizar(Long id, ReuniaoRequest request) {

        Reuniao reuniao = buscarEntidade(id);

        reuniao.setTitulo(request.titulo());
        reuniao.setData(request.data());
        reuniao.setStatus(request.status());
        reuniao.setAreas(request.areas());
        reuniao.setPontosChaves(request.pontosChaves());

        reuniao.setParticipantes(buscarColaboradores(request.participantes()));
        reuniao.setAcoes(buscarAcoes(request.acoes()));
        reuniao.setTotalAcoes(calcularTotalAcoes(request, reuniao.getAcoes()));

        return ReuniaoDTO.de(reuniaoRepository.save(reuniao));
    }

    public ReuniaoDTO atualizarParcial(Long id, ReuniaoRequest request) {

        Reuniao reuniao = buscarEntidade(id);

        if(request.titulo() != null)
            reuniao.setTitulo(request.titulo());
        if(request.data() != null)
            reuniao.setData(request.data());
        if(request.status() != null)
            reuniao.setStatus(request.status());
        if(request.areas() != null)
            reuniao.setAreas(request.areas());
        if(request.pontosChaves() != null)
            reuniao.setPontosChaves(request.pontosChaves());
        if(request.participantes() != null)
            reuniao.setParticipantes(buscarColaboradores(request.participantes()));
        if(request.acoes() != null) {
            reuniao.setAcoes(buscarAcoes(request.acoes()));
            reuniao.setTotalAcoes(reuniao.getAcoes().size());
        }

        return ReuniaoDTO.de(this.reuniaoRepository.save(reuniao));
    }

    public void deletar(Long id) {

        Reuniao reuniao = buscarEntidade(id);

        reuniaoRepository.delete(reuniao);
    }

    /** Uso interno da propria camada de servico — a web recebe DTO. */
    private Reuniao buscarEntidade(Long id) {
        return this.reuniaoRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Reunião", id));
    }

    /**
     * Troca os ids da request pelas entidades do banco. findAllById descarta
     * id inexistente em silencio, entao a contagem e conferida para o cliente
     * receber 404 em vez de uma reuniao salva com participante faltando.
     */
    private List<Colaborador> buscarColaboradores(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
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
            return new ArrayList<>();
        }

        List<Acao> acoes = acaoRepository.findAllById(ids);

        if (acoes.size() != ids.stream().distinct().count()) {
            throw new RecursoNaoEncontradoException(
                    "Ação não encontrada entre as ações informadas");
        }

        return acoes;
    }

    /** O total informado na request so vale quando nenhuma acao foi enviada. */
    private Integer calcularTotalAcoes(ReuniaoRequest request, List<Acao> acoes) {

        if (!acoes.isEmpty()) {
            return acoes.size();
        }

        if (request.acoes() == null) {
            return 0;
        }

        return request.acoes().size();
    }

}
