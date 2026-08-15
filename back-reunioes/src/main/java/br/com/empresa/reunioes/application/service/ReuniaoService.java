package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.domain.model.Colaborador;
import br.com.empresa.reunioes.domain.model.Reuniao;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReuniaoService{

    private final ReuniaoRepository reuniaoRepository;

    public Reuniao salvar(ReuniaoRequest request) {
        Reuniao reuniao = new Reuniao();
        reuniao.setTitulo(request.titulo());
        reuniao.setData(request.data());
        reuniao.setStatus(request.status());
        reuniao.setAreas(request.areas());

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

        return this.reuniaoRepository.save(reuniao);
    }

    public void deletar(Long id) {

        Reuniao reuniao = buscarPorId(id);

        reuniaoRepository.delete(reuniao);
    }

}
