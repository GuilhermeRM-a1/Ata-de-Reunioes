package br.com.empresa.reunioes.application.mapper;

import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.enums.StatusReuniao;
import br.com.empresa.reunioes.domain.enums.StatusTranscricao;
import br.com.empresa.reunioes.web.controller.dto.Acao.AcaoDTO;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorDTO;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.RelatorioReuniaoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static br.com.empresa.reunioes.application.mapper.AcaoMapper.acaoToDTO;
import static br.com.empresa.reunioes.application.mapper.ColaboradorMapper.colaboradorToDTO;

@Slf4j
@Component
public class RelatorioMapper {

    public static RelatorioReuniaoResponse montarRelatorio(Reuniao reuniao) {

        List<ColaboradorDTO> participantes = reuniao.getParticipantes() == null
                ? List.of()
                : reuniao.getParticipantes().stream().map(colaborador -> colaboradorToDTO(colaborador)).toList();

        List<AcaoDTO> acoes = reuniao.getAcoes() == null
                ? List.of()
                : reuniao.getAcoes().stream().map(acao -> acaoToDTO(acao)).toList();

        log.debug("Relatório da reunião {}: {} participantes, {} ações.",
                reuniao.getId(), participantes.size(), acoes.size());

        return new RelatorioReuniaoResponse(
                reuniao.getTitulo(),
                reuniao.getData(),
                reuniao.getResumo(),
                reuniao.getStatusTranscricao(),
                reuniao.getStatusReuniao(),
                participantes,
                reuniao.getAreas() == null ? List.of() : reuniao.getAreas(),
                reuniao.getPontosChaves() == null ? List.of() : reuniao.getPontosChaves(),
                acoes,
                // Contado na hora, a partir das acoes reais: o campo gravado pode
                // ter desencontrado se alguem removeu acao por outro caminho.
                acoes.size());
    }

}
