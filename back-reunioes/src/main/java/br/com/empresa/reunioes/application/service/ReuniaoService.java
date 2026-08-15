package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.domain.model.Acao;
import br.com.empresa.reunioes.domain.model.Colaborador;
import br.com.empresa.reunioes.domain.model.Reuniao;
import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReuniaoService{

    private final ReuniaoRepository reuniaoRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final AcaoRepository acaoRepository;

    public Reuniao salvar(ReuniaoRequest request) {
        Reuniao reuniao = new Reuniao();

        reuniao.setTitulo(request.titulo());
        reuniao.setData(request.data());
        reuniao.setStatus(request.status());
        reuniao.setAreas(request.areas());
        reuniao.setPontosChaves(request.pontosChaves());

        reuniao.setParticipantes(buscarColaboradores(request.participantes()));
        reuniao.setAcoes(buscarAcoes(request.acoes()));
        reuniao.setTotalAcoes(calcularTotalAcoes(request, reuniao.getAcoes()));

        return reuniaoRepository.save(reuniao);
    }

    public Reuniao buscarPorId(Long id) {
        return this.reuniaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reunião não encontrada"));

    }

    public List<Reuniao> listar() {
        return reuniaoRepository.findAll();
    }

    public Reuniao atualizar(Long id, ReuniaoRequest request) {

        Reuniao reuniao = buscarPorId(id);

        reuniao.setTitulo(request.titulo());
        reuniao.setData(request.data());
        reuniao.setStatus(request.status());
        reuniao.setAreas(request.areas());
        reuniao.setPontosChaves(request.pontosChaves());

        reuniao.setParticipantes(buscarColaboradores(request.participantes()));
        reuniao.setAcoes(buscarAcoes(request.acoes()));
        reuniao.setTotalAcoes(calcularTotalAcoes(request, reuniao.getAcoes()));

        return reuniaoRepository.save(reuniao);
    }

    public Reuniao atualizarParcial(Long id, ReuniaoRequest request) {

        Reuniao reuniao = buscarPorId(id);

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
        if(request.totalAcoes() != null)
            reuniao.setTotalAcoes(request.totalAcoes());

        return this.reuniaoRepository.save(reuniao);
    }

    public void deletar(Long id) {

        Reuniao reuniao = buscarPorId(id);

        reuniaoRepository.delete(reuniao);
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
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
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
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Ação não encontrada entre as ações informadas");
        }

        return acoes;
    }

    /** O total informado na request so vale quando nenhuma acao foi enviada. */
    private Integer calcularTotalAcoes(ReuniaoRequest request, List<Acao> acoes) {

        if (!acoes.isEmpty()) {
            return acoes.size();
        }

        return request.totalAcoes() != null ? request.totalAcoes() : 0;
    }

}
