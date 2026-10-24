package br.com.empresa.reunioes.reuniao.service;

import br.com.empresa.reunioes.application.reuniao.RelatorioService;
import br.com.empresa.reunioes.application.reuniao.ReuniaoService;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.exception.RecursoNaoEncontradoException;
import br.com.empresa.reunioes.domain.exception.RelatorioIndisponivelException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * O service so valida se a reuniao esta pronta para virar relatorio. A
 * montagem do relatorio em si e do RelatorioMapper, testado a parte.
 */
@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock
    ReuniaoService reuniaoService;

    @InjectMocks
    RelatorioService relatorioService;

    private Reuniao reuniaoComResumo(String resumo) {
        var reuniao = new Reuniao();
        reuniao.setId(7L);
        reuniao.setTitulo("Reunião de resultados");
        reuniao.setResumo(resumo);
        return reuniao;
    }

    @Test
    @DisplayName("Libera a reunião para o relatório quando o resumo da IA já chegou")
    void deveLiberarReuniaoComResumo() {
        var reuniao = reuniaoComResumo("Resumo executivo");
        when(reuniaoService.buscarPorId(7L)).thenReturn(reuniao);

        var validada = relatorioService.validarReuniaoParaRelatorio(7L);

        assertThat(validada).isSameAs(reuniao);
    }

    @ParameterizedTest(name = "resumo = \"{0}\"")
    @NullSource
    @ValueSource(strings = {"", "   ", "\n"})
    @DisplayName("Recusa o relatório, com 409, enquanto o resumo da IA não chegar")
    void deveRecusarReuniaoSemResumo(String resumo) {
        when(reuniaoService.buscarPorId(7L)).thenReturn(reuniaoComResumo(resumo));

        assertThatThrownBy(() -> relatorioService.validarReuniaoParaRelatorio(7L))
                .isInstanceOf(RelatorioIndisponivelException.class)
                .hasMessageContaining("7")
                .hasMessageContaining("/api/reunioes/7/ingestao");
    }

    @Test
    @DisplayName("Propaga o 404 quando a reunião não existe")
    void devePropagarQuandoReuniaoNaoExiste() {
        when(reuniaoService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Reunião não encontrada."));

        assertThatThrownBy(() -> relatorioService.validarReuniaoParaRelatorio(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
