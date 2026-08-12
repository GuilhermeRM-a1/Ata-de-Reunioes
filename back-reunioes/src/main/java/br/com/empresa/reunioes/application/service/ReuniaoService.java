package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.domain.model.Reuniao;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.controller.dto.ReuniaoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReuniaoService {

    private final ReuniaoRepository reuniaoRepository;

    public Reuniao salvar(ReuniaoRequest request) {
        Reuniao reuniao = new Reuniao();
        reuniao.setTitulo(request.titulo());
        reuniao.setData(request.data());
        reuniao.setResumo(request.resumo());
        reuniao.setStatus(request.status());
        reuniao.setParticipantes(request.participantes());
        reuniao.setAreas(request.areas());
        reuniao.setTotalAcoes(request.totalAcoes());

        return this.reuniaoRepository.save(reuniao);
    }

    public Reuniao buscarPorId(Long id) {
        return this.reuniaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reunião não encontrada"));

    }

    public List<Reuniao> listar() {
        return this.reuniaoRepository.findAll();
    }

    public Reuniao atualizar(ReuniaoRequest request) {
        return null;
    }

    public Reuniao atualizarParcial(ReuniaoRequest request) {
        return null;
    }

    public void deletar(Long id) {
    }

}
