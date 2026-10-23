package br.com.empresa.reunioes.web.reuniao.mapper;

import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.web.acao.dto.AcaoDTO;
import br.com.empresa.reunioes.web.reuniao.dto.ReuniaoDTO;

import java.util.List;

import static br.com.empresa.reunioes.web.acao.mapper.AcaoWebMapper.acaoToDTO;

public class ReuniaoWebMapper {

    public static ReuniaoDTO reuniaoToDTO(Reuniao reuniao) {

        // Registro antigo pode ter as colecoes nulas — sem isso a listagem quebra.
        List<String> participantes = reuniao.getParticipantes() == null
                ? List.of()
                : reuniao.getParticipantes().stream().map(Colaborador::getNome).toList();

        List<String> areas = reuniao.getAreas() == null ? List.of() : reuniao.getAreas();

        List<String> pontosChaves = reuniao.getPontosChaves() == null ? List.of() : reuniao.getPontosChaves();

        List<AcaoDTO> acoes = reuniao.getAcoes() != null
                ? reuniao.getAcoes().stream()
                .map(acao -> acaoToDTO(acao))
                .toList()
                : List.of();

        String resumo = reuniao.getResumo();

        return new ReuniaoDTO(reuniao.getId(),
                reuniao.getTitulo(),
                reuniao.getData(),
                reuniao.getStatusTranscricao(),
                reuniao.getStatusReuniao(),
                participantes,
                pontosChaves,
                acoes,
                areas,
                resumo,
                reuniao.getTotalAcoes());
    }

}
