package br.com.empresa.reunioes.application.mapper;

import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorDTO;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorRequest;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoDTO;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReuniaoMapper {

    public Reuniao toEntity(ReuniaoRequest request) {
        Reuniao reuniao = new Reuniao();

        reuniao.setTitulo(request.titulo());
        reuniao.setData(request.data());
        reuniao.setStatus(request.status());
        reuniao.setAreas(request.areas());
        reuniao.setPontosChaves(request.pontosChaves());

        return reuniao;
    }

    public ReuniaoDTO toDTO(Reuniao reuniao) {

        // Registro antigo pode ter as colecoes nulas — sem isso a listagem quebra.
        List<String> participantes = reuniao.getParticipantes() == null
                ? List.of()
                : reuniao.getParticipantes().stream().map(Colaborador::getNome).toList();

        List<String> areas = reuniao.getAreas() == null ? List.of() : reuniao.getAreas();

        return new ReuniaoDTO(reuniao.getId(),
                reuniao.getTitulo(),
                reuniao.getData(),
                reuniao.getStatus(),
                participantes,
                areas,
                reuniao.getTotalAcoes());
    }

    public void updateEntity(Reuniao reuniao, ReuniaoRequest request) {

        reuniao.setTitulo(request.titulo());
        reuniao.setData(request.data());
        reuniao.setStatus(request.status());
        reuniao.setAreas(request.areas());
        reuniao.setPontosChaves(request.pontosChaves());

    }

    public void updateParsiEntity(Reuniao reuniao, ReuniaoRequest request) {

        if (request.titulo() != null)
            reuniao.setTitulo(request.titulo());
        if (request.data() != null)
            reuniao.setData(request.data());
        if (request.status() != null)
            reuniao.setStatus(request.status());
        if (request.areas() != null)
            reuniao.setAreas(request.areas());
        if (request.pontosChaves() != null)
            reuniao.setPontosChaves(request.pontosChaves());
    }
}
