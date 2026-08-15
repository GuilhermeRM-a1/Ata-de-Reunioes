package br.com.empresa.reunioes.web.controller.dto.Reuniao;

import br.com.empresa.reunioes.domain.model.Colaborador;
import br.com.empresa.reunioes.domain.model.Reuniao;

import java.util.List;

public record ReuniaoDTO(String titulo,
                         String data,
                         String status,
                         List<String> participantes,
                         List<String> areas,
                         Integer totalAcoes) {

    public static ReuniaoDTO de(Reuniao reuniao) {
        return new ReuniaoDTO(reuniao.getTitulo(),
                reuniao.getData(),
                reuniao.getStatus(),
                reuniao.getParticipantes().stream().map(Colaborador::getNome).toList(),
                reuniao.getAreas(),
                reuniao.getTotalAcoes());
    }
}
