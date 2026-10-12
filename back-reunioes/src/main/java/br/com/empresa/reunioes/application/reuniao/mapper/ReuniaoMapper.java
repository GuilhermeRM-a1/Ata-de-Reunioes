package br.com.empresa.reunioes.application.reuniao.mapper;

import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.web.reuniao.dto.ReuniaoRequest;
import org.springframework.stereotype.Component;

@Component
public class ReuniaoMapper {

    public static Reuniao reuniaoToEntity(ReuniaoRequest request) {
        Reuniao reuniao = new Reuniao();

        reuniao.setTitulo(request.titulo());
        reuniao.setData(request.data());
        reuniao.setStatusTranscricao(request.statusTranscricao());
        reuniao.setStatusReuniao(request.statusReuniao());
        reuniao.setAreas(request.areas());
        reuniao.setPontosChaves(request.pontosChaves());
        reuniao.setResumo(request.resumo());

        return reuniao;
    }

    public static void reuniaoUpdateEntity(Reuniao reuniao, ReuniaoRequest request) {

        reuniao.setTitulo(request.titulo());
        reuniao.setData(request.data());
        reuniao.setStatusTranscricao(request.statusTranscricao());
        reuniao.setStatusReuniao(request.statusReuniao());
        reuniao.setAreas(request.areas());
        reuniao.setPontosChaves(request.pontosChaves());
        reuniao.setResumo(request.resumo());

    }

    public static void reuniaoUpdateParcialEntity(Reuniao reuniao, ReuniaoRequest request) {

        if (request.titulo() != null)
            reuniao.setTitulo(request.titulo());
        if (request.data() != null)
            reuniao.setData(request.data());
        if (request.statusTranscricao() != null)
            reuniao.setStatusTranscricao(request.statusTranscricao());
        if (request.statusReuniao() != null)
            reuniao.setStatusReuniao(request.statusReuniao());
        if (request.areas() != null)
            reuniao.setAreas(request.areas());
        if (request.pontosChaves() != null)
            reuniao.setPontosChaves(request.pontosChaves());
        if (request.resumo() != null)
            reuniao.setResumo(request.resumo());
    }
}
