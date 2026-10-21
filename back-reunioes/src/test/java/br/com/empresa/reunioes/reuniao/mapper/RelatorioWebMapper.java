package br.com.empresa.reunioes.reuniao.mapper;

import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.web.reuniao.mapper.RelatorioMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RelatorioMapperTest {

    @Test
    void deveMontarRelatorioComListasVaziasQuandoForemNull() {
        // Arrange
        Reuniao reuniao = new Reuniao();
        reuniao.setTitulo("Reunião de planejamento");
        reuniao.setParticipantes(null);
        reuniao.setAcoes(null);
        reuniao.setAreas(null);
        reuniao.setPontosChaves(null);

        // Act
        var relatorio = RelatorioMapper.montarRelatorio(reuniao);

        // Assert
        assertNotNull(relatorio);
        assertEquals("Reunião de planejamento", relatorio.titulo());
        assertTrue(relatorio.participantes().isEmpty());
        assertTrue(relatorio.acoes().isEmpty());
        assertTrue(relatorio.areas().isEmpty());
        assertTrue(relatorio.pontosChaves().isEmpty());
        assertEquals(0, relatorio.totalAcoes());
    }
}