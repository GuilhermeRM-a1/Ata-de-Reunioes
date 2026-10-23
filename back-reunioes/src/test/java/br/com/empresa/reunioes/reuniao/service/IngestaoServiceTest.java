package br.com.empresa.reunioes.reuniao.service;

import br.com.empresa.reunioes.application.reuniao.IngestaoService;
import br.com.empresa.reunioes.application.reuniao.ReuniaoService;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.exception.RecursoNaoEncontradoException;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.reuniao.dto.request.ReuniaoResumoRequest;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unitario puro, sem banco: o repositorio devolve o que recebeu, entao o que
 * se confere aqui e o que o service gravou na entidade antes do save.
 */
@ExtendWith(MockitoExtension.class)
class IngestaoServiceTest {

    @Mock
    ReuniaoRepository reuniaoRepository;

    @Mock
    ReuniaoService reuniaoService;

    @InjectMocks
    IngestaoService ingestaoService;

    private Reuniao reuniao;

    @BeforeEach
    void prepararReuniao() {
        reuniao = new Reuniao();
        reuniao.setId(1L);
        reuniao.setTitulo("Reunião de planejamento");
    }

    private void simularReuniaoExistente() {
        when(reuniaoService.buscarPorId(1L)).thenReturn(reuniao);
        when(reuniaoRepository.save(any(Reuniao.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    @Test
    @DisplayName("Grava resumo e transcrição na reunião, sem os espaços das pontas")
    void deveGravarResumoETranscricao() {
        simularReuniaoExistente();
        var request = new ReuniaoResumoRequest("  Resumo executivo  ", "\nTranscrição completa\n");

        var salva = ingestaoService.receberAnalise(1L, request);

        assertThat(salva.getResumo()).isEqualTo("Resumo executivo");
        assertThat(salva.getTranscricao()).isEqualTo("Transcrição completa");
        verify(reuniaoRepository).save(reuniao);
    }

    @Test
    @DisplayName("Reingestão substitui o resumo anterior")
    void deveSubstituirResumoAnterior() {
        reuniao.setResumo("Resumo antigo");
        simularReuniaoExistente();

        var salva = ingestaoService.receberAnalise(1L, new ReuniaoResumoRequest("Resumo novo", null));

        assertThat(salva.getResumo()).isEqualTo("Resumo novo");
    }

    @ParameterizedTest(name = "transcricao = \"{0}\"")
    @NullSource
    @ValueSource(strings = {"", "   ", "\n\t"})
    @DisplayName("Reenvio só do resumo preserva a transcrição já gravada")
    void devePreservarTranscricaoQuandoNaoVierNova(String transcricao) {
        reuniao.setTranscricao("Transcrição original");
        simularReuniaoExistente();

        var salva = ingestaoService.receberAnalise(1L, new ReuniaoResumoRequest("Resumo", transcricao));

        assertThat(salva.getResumo()).isEqualTo("Resumo");
        assertThat(salva.getTranscricao()).isEqualTo("Transcrição original");
    }

    @Test
    @DisplayName("Devolve exatamente a entidade que o repositório gravou")
    void deveDevolverEntidadeSalvaPeloRepositorio() {
        var persistida = new Reuniao();
        persistida.setId(1L);
        persistida.setResumo("Resumo");

        when(reuniaoService.buscarPorId(1L)).thenReturn(reuniao);
        when(reuniaoRepository.save(reuniao)).thenReturn(persistida);

        var salva = ingestaoService.receberAnalise(1L, new ReuniaoResumoRequest("Resumo", null));

        assertThat(salva).isSameAs(persistida);
    }

    @Test
    @DisplayName("Propaga o 404 da reunião e não grava nada")
    void devePropagarQuandoReuniaoNaoExiste() {
        when(reuniaoService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Reunião não encontrada."));

        assertThatThrownBy(() -> ingestaoService.receberAnalise(99L, new ReuniaoResumoRequest("Resumo", null)))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(reuniaoRepository, never()).save(any());
    }
}
