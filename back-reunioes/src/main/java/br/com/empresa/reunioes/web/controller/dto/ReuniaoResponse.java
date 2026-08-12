package br.com.empresa.reunioes.web.controller.dto;

import br.com.empresa.reunioes.domain.model.Colaborador;
import br.com.empresa.reunioes.domain.model.Reuniao;
import br.com.empresa.reunioes.domain.model.StatusReuniao;

import java.util.List;

public record ReuniaoResponse(String titulo,
         String data,
         String resumo,
         StatusReuniao status,
         List<Colaborador>participantes,
         List<String> areas,
         Integer totalAcoes) {

    public static ReuniaoResponse de(Reuniao reuniao) {
        return new ReuniaoResponse(reuniao.getTitulo(),
                reuniao.getData(),
                reuniao.getResumo(),
                reuniao.getStatus(),
                reuniao.getParticipantes(),
                reuniao.getAreas(),
                reuniao.getTotalAcoes());
    }
}
