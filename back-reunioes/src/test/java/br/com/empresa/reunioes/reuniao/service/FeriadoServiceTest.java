package br.com.empresa.reunioes.reuniao.service;

import br.com.empresa.reunioes.application.reuniao.FeriadoService;
import br.com.empresa.reunioes.application.reuniao.ReuniaoService;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.exception.RecursoNaoEncontradoException;
import br.com.empresa.reunioes.infra.client.FeriadoClient;
import br.com.empresa.reunioes.infra.client.dto.FeriadoClientResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unitario puro: o cliente Feign e o service de reuniao sao mocks, entao o
 * teste nao sobe contexto, nao toca banco e nao chama a BrasilAPI.
 */
@ExtendWith(MockitoExtension.class)
class FeriadoServiceTest {

    @Mock
    FeriadoClient feriadoClient;

    @Mock
    ReuniaoService reuniaoService;

    @InjectMocks
    FeriadoService feriadoService;

    private static final FeriadoClientResponse NATAL =
            new FeriadoClientResponse("2026-12-25", "Natal", "national", "sexta-feira");

    private static final FeriadoClientResponse CONFRATERNIZACAO =
            new FeriadoClientResponse("2026-01-01", "Confraternização mundial", "national", "quinta-feira");

    private Reuniao reuniaoCom(String data) {
        var reuniao = new Reuniao();
        reuniao.setId(1L);
        reuniao.setTitulo("Reunião de fechamento");
        reuniao.setData(data);
        return reuniao;
    }

    @Test
    @DisplayName("Marca a reunião como feriado quando a data bate com o calendário")
    void deveApontarFeriadoQuandoDataCoincide() {
        when(reuniaoService.buscarPorId(1L)).thenReturn(reuniaoCom("2026-12-25"));
        when(feriadoClient.listarPorAno(2026)).thenReturn(List.of(CONFRATERNIZACAO, NATAL));

        var resultado = feriadoService.verificarReuniao(1L);

        assertThat(resultado.feriado()).isTrue();
        assertThat(resultado.reuniaoId()).isEqualTo(1L);
        assertThat(resultado.titulo()).isEqualTo("Reunião de fechamento");
        assertThat(resultado.data()).isEqualTo("2026-12-25");
        assertThat(resultado.nomeFeriado()).isEqualTo("Natal");
        assertThat(resultado.diaSemana()).isEqualTo("sexta-feira");
    }

    @Test
    @DisplayName("Devolve dia comum, sem nome de feriado, quando a data não está no calendário")
    void deveDevolverDiaComumQuandoDataNaoEhFeriado() {
        when(reuniaoService.buscarPorId(1L)).thenReturn(reuniaoCom("2026-06-02"));
        when(feriadoClient.listarPorAno(2026)).thenReturn(List.of(CONFRATERNIZACAO, NATAL));

        var resultado = feriadoService.verificarReuniao(1L);

        assertThat(resultado.feriado()).isFalse();
        assertThat(resultado.data()).isEqualTo("2026-06-02");
        assertThat(resultado.nomeFeriado()).isNull();
        assertThat(resultado.diaSemana()).isNull();
    }

    @Test
    @DisplayName("Aceita data com hora e consulta o ano certo na API externa")
    void deveExtrairDiaDeDataComHora() {
        when(reuniaoService.buscarPorId(1L)).thenReturn(reuniaoCom("2026-12-25T09:00:00"));
        when(feriadoClient.listarPorAno(2026)).thenReturn(List.of(NATAL));

        var resultado = feriadoService.verificarReuniao(1L);

        assertThat(resultado.feriado()).isTrue();
        assertThat(resultado.data()).isEqualTo("2026-12-25");
        verify(feriadoClient).listarPorAno(2026);
    }

    @Test
    @DisplayName("Devolve dia comum quando a API externa não traz feriado nenhum")
    void deveDevolverDiaComumQuandoListaVazia() {
        when(reuniaoService.buscarPorId(1L)).thenReturn(reuniaoCom("2026-12-25"));
        when(feriadoClient.listarPorAno(2026)).thenReturn(List.of());

        var resultado = feriadoService.verificarReuniao(1L);

        assertThat(resultado.feriado()).isFalse();
    }

    @ParameterizedTest(name = "data = \"{0}\"")
    @NullSource
    @ValueSource(strings = {"", "2026", "25/12/2026", "ontem à noite"})
    @DisplayName("Rejeita reunião sem data reconhecível e nem chama a API externa")
    void deveRejeitarDataIrreconhecivel(String data) {
        when(reuniaoService.buscarPorId(1L)).thenReturn(reuniaoCom(data));

        assertThatThrownBy(() -> feriadoService.verificarReuniao(1L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("1")
                .hasMessageContaining("aaaa-MM-dd");

        verify(feriadoClient, never()).listarPorAno(anyInt());
    }

    @Test
    @DisplayName("Propaga o 404 do service de reunião sem consultar a API externa")
    void devePropagarQuandoReuniaoNaoExiste() {
        when(reuniaoService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Reunião não encontrada."));

        assertThatThrownBy(() -> feriadoService.verificarReuniao(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(feriadoClient, never()).listarPorAno(anyInt());
    }
}
