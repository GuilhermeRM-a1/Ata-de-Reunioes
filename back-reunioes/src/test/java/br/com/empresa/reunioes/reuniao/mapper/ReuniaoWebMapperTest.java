package br.com.empresa.reunioes.reuniao.mapper;

import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.web.reuniao.dto.ReuniaoDTO;
import br.com.empresa.reunioes.web.reuniao.mapper.ReuniaoWebMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReuniaoWebMapperTest {

    @Test
    void deveConverterReuniaoParaDTO() {
        // Arrange
        Reuniao reuniao = new Reuniao();
        reuniao.setId(1L);
        reuniao.setTitulo("Reunião de planejamento");
        reuniao.setData("2026-10-19");
        reuniao.setResumo("Resumo da reunião");
        reuniao.setAreas(List.of("TI"));
        reuniao.setPontosChaves(List.of("Definir metas"));

        // Act
        ReuniaoDTO dto = ReuniaoWebMapper.reuniaoToDTO(reuniao);

        // Assert
        assertAll(
                () -> assertEquals(1L, dto.id()),
                () -> assertEquals("Reunião de planejamento", dto.titulo()),
                () -> assertEquals("2026-10-19", dto.data()),
                () -> assertEquals("Resumo da reunião", dto.resumo()),
                () -> assertEquals(List.of("TI"), dto.areas()),
                () -> assertEquals(List.of("Definir metas"), dto.pontosChaves()),
                () -> assertNotNull(dto.participantes()),
                () -> assertNotNull(dto.acoes())
        );
    }

    @Test
    void deveRetornarColecoesVaziasQuandoColecoesDaReuniaoForemNulas() {
        // Arrange
        Reuniao reuniao = new Reuniao();
        reuniao.setAreas(null);
        reuniao.setPontosChaves(null);
        reuniao.setParticipantes(null);
        reuniao.setAcoes(null);

        // Act
        ReuniaoDTO dto = ReuniaoWebMapper.reuniaoToDTO(reuniao);

        // Assert
        assertAll(
                () -> assertEquals(List.of(), dto.areas()),
                () -> assertEquals(List.of(), dto.pontosChaves()),
                () -> assertEquals(List.of(), dto.participantes()),
                () -> assertEquals(List.of(), dto.acoes())
        );
    }
}