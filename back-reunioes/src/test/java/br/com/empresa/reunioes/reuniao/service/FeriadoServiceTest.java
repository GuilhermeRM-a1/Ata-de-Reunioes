package br.com.empresa.reunioes.reuniao.service;

import br.com.empresa.reunioes.application.reuniao.FeriadoService;
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
 * Unitario puro: o cliente Feign e mock, entao o teste nao sobe contexto,
 * nao toca banco e nao chama a BrasilAPI.
 */
@ExtendWith(MockitoExtension.class)
class FeriadoServiceTest {

    @Mock
    FeriadoClient feriadoClient;

    @InjectMocks
    FeriadoService feriadoService;

    private static final FeriadoClientResponse NATAL =
            new FeriadoClientResponse("2026-12-25", "Natal", "national", "sexta-feira");

    private static final FeriadoClientResponse CONFRATERNIZACAO =
            new FeriadoClientResponse("2026-01-01", "Confraternização mundial", "national", "quinta-feira");

    @Test
    @DisplayName("Devolve o feriado quando a data bate com o calendário")
    void deveDevolverFeriadoQuandoDataCoincide() {
        when(feriadoClient.listarPorAno(2026)).thenReturn(List.of(CONFRATERNIZACAO, NATAL));

        var resultado = feriadoService.buscarPorData("2026-12-25");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().name()).isEqualTo("Natal");
        assertThat(resultado.get().weekday()).isEqualTo("sexta-feira");
    }

    @Test
    @DisplayName("Devolve vazio quando a data não está no calendário")
    void deveDevolverVazioQuandoDataNaoEhFeriado() {
        when(feriadoClient.listarPorAno(2026)).thenReturn(List.of(CONFRATERNIZACAO, NATAL));

        assertThat(feriadoService.buscarPorData("2026-06-02")).isEmpty();
    }

    @Test
    @DisplayName("Aceita data com hora e consulta o ano certo na API externa")
    void deveExtrairDiaDeDataComHora() {
        when(feriadoClient.listarPorAno(2026)).thenReturn(List.of(NATAL));

        var resultado = feriadoService.buscarPorData("2026-12-25T09:00:00");

        assertThat(resultado).isPresent();
        verify(feriadoClient).listarPorAno(2026);
    }

    @Test
    @DisplayName("Devolve vazio quando a API externa não traz feriado nenhum")
    void deveDevolverVazioQuandoListaVazia() {
        when(feriadoClient.listarPorAno(2026)).thenReturn(List.of());

        assertThat(feriadoService.buscarPorData("2026-12-25")).isEmpty();
    }

    @ParameterizedTest(name = "data = \"{0}\"")
    @NullSource
    @ValueSource(strings = {"", "2026", "25/12/2026", "ontem à noite"})
    @DisplayName("Rejeita data sem formato reconhecível e nem chama a API externa")
    void deveRejeitarDataIrreconhecivel(String data) {
        assertThatThrownBy(() -> feriadoService.buscarPorData(data))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("aaaa-MM-dd");

        verify(feriadoClient, never()).listarPorAno(anyInt());
    }
}