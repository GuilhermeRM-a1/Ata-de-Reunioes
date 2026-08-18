package br.com.empresa.reunioes.web.controller.dto.Reuniao;

import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.entity.Reuniao;

import java.util.List;

public record ReuniaoDTO(Long id,
                         String titulo,
                         String data,
                         String status,
                         List<String> participantes,
                         List<String> areas,
                         Integer totalAcoes) {

    public static ReuniaoDTO de(Reuniao reuniao) {

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
}
