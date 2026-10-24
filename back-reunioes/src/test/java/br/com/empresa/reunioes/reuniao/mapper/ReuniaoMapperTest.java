package br.com.empresa.reunioes.reuniao.mapper;

import br.com.empresa.reunioes.application.reuniao.mapper.ReuniaoMapper;
import br.com.empresa.reunioes.domain.enums.StatusReuniao;
import br.com.empresa.reunioes.domain.enums.StatusTranscricao;
import br.com.empresa.reunioes.web.reuniao.dto.request.ReuniaoRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReuniaoMapperTest {

    @Test
    void deveConverterRequestParaEntity() {
        var request = new ReuniaoRequest(
                "Reunião de planejamento",
                "2026-10-19",
                StatusTranscricao.RECEBIDA,
                StatusReuniao.PENDENTE,
                List.of("TI"),
                List.of("Definir metas"),
                List.of(1L, 2L),
                List.of(3L),
                "Resumo da reunião"
        );

        var reuniao = ReuniaoMapper.reuniaoToEntity(request);

        assertThat(reuniao.getId()).isNull();
        assertThat(reuniao.getTitulo()).isEqualTo("Reunião de planejamento");
        assertThat(reuniao.getData()).isEqualTo("2026-10-19");
        assertThat(reuniao.getStatusTranscricao()).isEqualTo(StatusTranscricao.RECEBIDA);
        assertThat(reuniao.getStatusReuniao()).isEqualTo(StatusReuniao.PENDENTE);
        assertThat(reuniao.getAreas()).containsExactly("TI");
        assertThat(reuniao.getPontosChaves()).containsExactly("Definir metas");
        assertThat(reuniao.getResumo()).isEqualTo("Resumo da reunião");
    }
}