package br.com.empresa.reunioes.reuniao.mapper;

import br.com.empresa.reunioes.application.reuniao.mapper.ReuniaoMapper;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.enums.StatusReuniao;
import br.com.empresa.reunioes.domain.enums.StatusTranscricao;
import br.com.empresa.reunioes.web.reuniao.dto.ReuniaoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReuniaoMapperTest {

    @Test
    void deveConverterRequestParaEntity() {
        // Arrange
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

        // Act
        Reuniao reuniao = ReuniaoMapper.reuniaoToEntity(request);

        // Assert
        assertAll(
                () -> assertEquals(request.titulo(), reuniao.getTitulo()),
                () -> assertEquals(request.data(), reuniao.getData()),
                () -> assertEquals(request.statusTranscricao(), reuniao.getStatusTranscricao()),
                () -> assertEquals(request.statusReuniao(), reuniao.getStatusReuniao()),
                () -> assertEquals(request.areas(), reuniao.getAreas()),
                () -> assertEquals(request.pontosChaves(), reuniao.getPontosChaves()),
                () -> assertEquals(request.resumo(), reuniao.getResumo())
        );
    }
}