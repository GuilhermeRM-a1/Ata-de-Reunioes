package br.com.empresa.reunioes.reuniao.mapper;

import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.web.reuniao.mapper.RelatorioMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RelatorioMapperTest {

    @Test
    void deveMontarRelatorioComListasVaziasQuandoForemNull() {
        var reuniao = new Reuniao();
        reuniao.setTitulo("Reunião de planejamento");
        reuniao.setParticipantes(null);
        reuniao.setAcoes(null);
        reuniao.setAreas(null);
        reuniao.setPontosChaves(null);

        var relatorio = RelatorioMapper.montarRelatorio(reuniao);

        assertThat(relatorio.titulo()).isEqualTo("Reunião de planejamento");
        assertThat(relatorio.participantes()).isEmpty();
        assertThat(relatorio.acoes()).isEmpty();
        assertThat(relatorio.areas()).isEmpty();
        assertThat(relatorio.pontosChaves()).isEmpty();
        assertThat(relatorio.totalAcoes()).isZero();
    }

    @Test
    void deveMontarRelatorioComDadosDaReuniao() {
        var reuniao = new Reuniao();
        reuniao.setTitulo("Reunião de planejamento");
        reuniao.setAreas(List.of("TI", "RH"));
        reuniao.setPontosChaves(List.of("Definir metas"));
        reuniao.setAcoes(List.of());
        reuniao.setParticipantes(List.of());

        var relatorio = RelatorioMapper.montarRelatorio(reuniao);

        assertThat(relatorio.titulo()).isEqualTo("Reunião de planejamento");
        assertThat(relatorio.areas()).containsExactly("TI", "RH");
        assertThat(relatorio.pontosChaves()).containsExactly("Definir metas");
        assertThat(relatorio.totalAcoes()).isEqualTo(reuniao.getAcoes().size());
    }
}