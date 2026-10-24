package br.com.empresa.reunioes.reuniao;

import br.com.empresa.reunioes.application.reuniao.FeriadoService;
import br.com.empresa.reunioes.application.reuniao.IngestaoService;
import br.com.empresa.reunioes.application.reuniao.RelatorioService;
import br.com.empresa.reunioes.application.reuniao.ReuniaoService;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.enums.StatusReuniao;
import br.com.empresa.reunioes.domain.enums.StatusTranscricao;
import br.com.empresa.reunioes.domain.exception.RecursoNaoEncontradoException;
import br.com.empresa.reunioes.domain.exception.RelatorioIndisponivelException;
import br.com.empresa.reunioes.web.reuniao.ReuniaoController;
import br.com.empresa.reunioes.web.reuniao.dto.ReuniaoFeriadoDTO;
import br.com.empresa.reunioes.web.reuniao.dto.request.ReuniaoPatchRequest;
import br.com.empresa.reunioes.web.reuniao.dto.request.ReuniaoRequest;
import br.com.empresa.reunioes.web.reuniao.dto.request.ReuniaoResumoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReuniaoController.class)
@DisplayName("Controller de reuniões")
class ReuniaoControllerTest {

    private static final String URL_BASE = "/api/reunioes";
    private static final String URL_POR_ID = URL_BASE + "/{id}";
    private static final String URL_INGESTAO = URL_POR_ID + "/ingestao";
    private static final String URL_RELATORIO = URL_POR_ID + "/relatorio";
    private static final String URL_FERIADO = URL_POR_ID + "/feriado";

    private static final String TITULO = "Reunião de Alinhamento";
    private static final String MENSAGEM_NAO_ENCONTRADA = "Reunião não encontrada.";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ReuniaoService reuniaoService;

    @MockitoBean
    IngestaoService ingestaoService;

    @MockitoBean
    RelatorioService relatorioService;

    @MockitoBean
    FeriadoService feriadoService;

    private Reuniao criarReuniao() {
        var reuniao = new Reuniao();
        reuniao.setId(1L);
        reuniao.setTitulo(TITULO);
        reuniao.setData("2026-06-01T10:00:00");
        reuniao.setStatusTranscricao(StatusTranscricao.CONCLUIDA);
        reuniao.setStatusReuniao(StatusReuniao.PENDENTE);
        reuniao.setParticipantes(List.of());
        reuniao.setAcoes(List.of());
        reuniao.setAreas(List.of("TI"));
        reuniao.setPontosChaves(List.of("Ponto chave 1"));
        reuniao.setResumo("Resumo executivo");
        return reuniao;
    }

    private ReuniaoRequest requestValido() {
        return new ReuniaoRequest(
                TITULO,
                "2026-06-01T10:00:00",
                StatusTranscricao.CONCLUIDA,
                StatusReuniao.PENDENTE,
                List.of("TI"),
                List.of("Ponto chave 1"),
                List.of(),
                List.of(),
                "Resumo executivo"
        );
    }

    private ReuniaoRequest requestInvalido() {
        return new ReuniaoRequest(
                "",
                "",
                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null
        );
    }

    private String json(Object objeto) {
        return objectMapper.writeValueAsString(objeto);
    }

    private ResultActions enviarJson(
            MockHttpServletRequestBuilder metodo,
            Object corpo) throws Exception {
        return mockMvc.perform(metodo
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(corpo)));
    }

    @Nested
    @DisplayName("Consultas de reuniões")
    class Consultas {

        @Test
        @DisplayName("Dada paginação, quando listar, então retorna 200 com a página e repassa page e size ao service")
        void paginacao_deveRetornarPaginaDeReunioes() throws Exception {

            when(reuniaoService.listar(any()))
                    .thenReturn(new PageImpl<>(List.of(criarReuniao())));


            ResultActions resposta = mockMvc.perform(get(URL_BASE)
                    .param("page", "0")
                    .param("size", "10"));


            resposta.andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].titulo").value(TITULO))
                    .andExpect(jsonPath("$.totalElements").value(1));

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(reuniaoService).listar(captor.capture());
            assertThat(captor.getValue().getPageNumber()).isZero();
            assertThat(captor.getValue().getPageSize()).isEqualTo(10);
        }

        @Test
        @DisplayName("Dado id existente, quando buscar, então retorna 200 com os dados da reunião")
        void idExistente_deveRetornarReuniao() throws Exception {

            when(reuniaoService.buscarPorId(1L)).thenReturn(criarReuniao());


            ResultActions resposta = mockMvc.perform(get(URL_POR_ID, 1L));


            resposta.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.titulo").value(TITULO));

            verify(reuniaoService).buscarPorId(1L);
        }

        @Test
        @DisplayName("Dado id inexistente, quando buscar, então retorna 404")
        void idInexistente_deveRetornarNotFound() throws Exception {

            when(reuniaoService.buscarPorId(99L))
                    .thenThrow(new RecursoNaoEncontradoException(MENSAGEM_NAO_ENCONTRADA));


            ResultActions resposta = mockMvc.perform(get(URL_POR_ID, 99L));


            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.detail").value(MENSAGEM_NAO_ENCONTRADA));

            verify(reuniaoService).buscarPorId(99L);
        }
    }

    @Nested
    @DisplayName("Cadastro de reuniões")
    class Cadastro {

        @Test
        @DisplayName("Dada requisição válida, quando cadastrar, então retorna 201 com Location e entrega os dados ao service")
        void requisicaoValida_deveRetornarCreatedComLocation() throws Exception {

            when(reuniaoService.salvar(any())).thenReturn(criarReuniao());


            ResultActions resposta = enviarJson(post(URL_BASE), requestValido());


            resposta.andExpect(status().isCreated())
                    .andExpect(header().string("Location", endsWith("/api/reunioes/1")))
                    .andExpect(jsonPath("$.titulo").value(TITULO));

            ArgumentCaptor<ReuniaoRequest> captor = ArgumentCaptor.forClass(ReuniaoRequest.class);
            verify(reuniaoService).salvar(captor.capture());
            assertThat(captor.getValue().titulo()).isEqualTo(TITULO);
        }

        @Test
        @DisplayName("Dada requisição inválida, quando cadastrar, então retorna 400 com os campos inválidos e não chama o service")
        void requisicaoInvalida_deveRetornarBadRequest() throws Exception {

            var request = requestInvalido();


            ResultActions resposta = enviarJson(post(URL_BASE), request);


            resposta.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Campos inválidos"))
                    .andExpect(jsonPath("$.campos").isArray());

            verifyNoInteractions(reuniaoService);
        }
    }

    @Nested
    @DisplayName("Atualização de reuniões")
    class Atualizacao {

        @Test
        @DisplayName("Dada requisição válida, quando atualizar, então retorna 200 com a reunião atualizada")
        void requisicaoValida_deveRetornarReuniaoAtualizada() throws Exception {

            when(reuniaoService.atualizar(eq(1L), any())).thenReturn(criarReuniao());


            ResultActions resposta = enviarJson(put(URL_POR_ID, 1L), requestValido());


            resposta.andExpect(status().isOk())
                    .andExpect(jsonPath("$.titulo").value(TITULO));

            ArgumentCaptor<ReuniaoRequest> captor = ArgumentCaptor.forClass(ReuniaoRequest.class);
            verify(reuniaoService).atualizar(eq(1L), captor.capture());
            assertThat(captor.getValue().titulo()).isEqualTo(TITULO);
        }

        @Test
        @DisplayName("Dada requisição inválida, quando atualizar, então retorna 400 e não chama o service")
        void requisicaoInvalida_deveRetornarBadRequest() throws Exception {

            var request = requestInvalido();


            ResultActions resposta = enviarJson(put(URL_POR_ID, 1L), request);


            resposta.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Campos inválidos"));

            verifyNoInteractions(reuniaoService);
        }

        @Test
        @DisplayName("Dado id inexistente, quando atualizar, então retorna 404")
        void idInexistente_deveRetornarNotFound() throws Exception {

            when(reuniaoService.atualizar(eq(99L), any()))
                    .thenThrow(new RecursoNaoEncontradoException(MENSAGEM_NAO_ENCONTRADA));


            ResultActions resposta = enviarJson(put(URL_POR_ID, 99L), requestValido());


            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Dado apenas o título, quando atualizar parcialmente, então retorna 200 e entrega só o título ao service")
        void apenasTitulo_deveRetornarReuniaoAtualizada() throws Exception {

            var request = new ReuniaoPatchRequest(
                    "Novo título", null, null, null,
                    null, null, null, null, null
            );
            when(reuniaoService.atualizarParcial(eq(1L), any())).thenReturn(criarReuniao());


            ResultActions resposta = enviarJson(patch(URL_POR_ID, 1L), request);


            resposta.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));

            ArgumentCaptor<ReuniaoRequest> captor = ArgumentCaptor.forClass(ReuniaoRequest.class);
            verify(reuniaoService).atualizarParcial(eq(1L), captor.capture());
            assertThat(captor.getValue().titulo()).isEqualTo("Novo título");
            assertThat(captor.getValue().resumo()).isNull();
        }

        @Test
        @DisplayName("Dado título com menos de 5 caracteres, quando atualizar parcialmente, então retorna 400 e não chama o service")
        void tituloCurto_aoAtualizarParcialmente_deveRetornarBadRequest() throws Exception {

            var request = new ReuniaoPatchRequest(
                    "abc", null, null, null,
                    null, null, null, null, null
            );


            ResultActions resposta = enviarJson(patch(URL_POR_ID, 1L), request);


            resposta.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Campos inválidos"))
                    .andExpect(jsonPath("$.campos[0].campo").value("titulo"));

            verifyNoInteractions(reuniaoService);
        }

        @Test
        @DisplayName("Dado id inexistente, quando atualizar parcialmente, então retorna 404")
        void idInexistente_aoAtualizarParcialmente_deveRetornarNotFound() throws Exception {

            var request = new ReuniaoPatchRequest(
                    "Novo título", null, null, null,
                    null, null, null, null, null
            );
            when(reuniaoService.atualizarParcial(eq(99L), any()))
                    .thenThrow(new RecursoNaoEncontradoException(MENSAGEM_NAO_ENCONTRADA));


            ResultActions resposta = enviarJson(patch(URL_POR_ID, 99L), request);


            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("Ingestão de dados da reunião")
    class Ingestao {

        @Test
        @DisplayName("Dados resumo e transcrição, quando ingerir, então retorna 202 e entrega os dados ao service")
        void dadosValidos_deveRetornarAccepted() throws Exception {

            var request = new ReuniaoResumoRequest(
                    "Resumo executivo da reunião",
                    "Transcrição completa da reunião"
            );
            when(ingestaoService.receberAnalise(eq(1L), any())).thenReturn(criarReuniao());


            ResultActions resposta = enviarJson(patch(URL_INGESTAO, 1L), request);


            resposta.andExpect(status().isAccepted());

            ArgumentCaptor<ReuniaoResumoRequest> captor = ArgumentCaptor.forClass(ReuniaoResumoRequest.class);
            verify(ingestaoService).receberAnalise(eq(1L), captor.capture());
            assertThat(captor.getValue().resumo()).isEqualTo("Resumo executivo da reunião");
        }

        @Test
        @DisplayName("Dado resumo em branco, quando ingerir, então retorna 400 indicando o campo e não chama o service")
        void resumoEmBranco_deveRetornarBadRequest() throws Exception {

            var request = new ReuniaoResumoRequest("   ", "Transcrição");


            ResultActions resposta = enviarJson(patch(URL_INGESTAO, 1L), request);


            resposta.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Campos inválidos"))
                    .andExpect(jsonPath("$.campos[0].campo").value("resumo"));

            verifyNoInteractions(ingestaoService);
        }

        @Test
        @DisplayName("Dada reunião inexistente, quando ingerir, então retorna 404")
        void reuniaoInexistente_deveRetornarNotFound() throws Exception {

            when(ingestaoService.receberAnalise(eq(99L), any()))
                    .thenThrow(new RecursoNaoEncontradoException(MENSAGEM_NAO_ENCONTRADA));


            ResultActions resposta = enviarJson(
                    patch(URL_INGESTAO, 99L),
                    new ReuniaoResumoRequest("Resumo", null));


            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("Geração de relatórios")
    class Relatorios {

        @Test
        @DisplayName("Dada reunião com resumo da IA, quando gerar relatório, então retorna 200 com os dados da reunião")
        void reuniaoComResumo_deveRetornarRelatorio() throws Exception {

            when(relatorioService.validarReuniaoParaRelatorio(1L)).thenReturn(criarReuniao());


            ResultActions resposta = mockMvc.perform(get(URL_RELATORIO, 1L));


            resposta.andExpect(status().isOk())
                    .andExpect(jsonPath("$.titulo").value(TITULO));

            verify(relatorioService).validarReuniaoParaRelatorio(1L);
        }

        @Test
        @DisplayName("Dada reunião sem resumo da IA, quando gerar relatório, então retorna 409")
        void reuniaoSemResumo_deveRetornarConflict() throws Exception {

            when(relatorioService.validarReuniaoParaRelatorio(1L))
                    .thenThrow(new RelatorioIndisponivelException(
                            "A reunião 1 ainda não recebeu o resumo da IA."));


            ResultActions resposta = mockMvc.perform(get(URL_RELATORIO, 1L));


            resposta.andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.title").value("Relatório indisponível"))
                    .andExpect(jsonPath("$.detail")
                            .value("A reunião 1 ainda não recebeu o resumo da IA."));
        }

        @Test
        @DisplayName("Dada reunião inexistente, quando gerar relatório, então retorna 404")
        void reuniaoInexistente_deveRetornarNotFound() throws Exception {

            when(relatorioService.validarReuniaoParaRelatorio(99L))
                    .thenThrow(new RecursoNaoEncontradoException(MENSAGEM_NAO_ENCONTRADA));


            ResultActions resposta = mockMvc.perform(get(URL_RELATORIO, 99L));


            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("Verificação de feriados")
    class Feriados {

        @Test
        @DisplayName("Dada reunião em feriado, quando verificar, então retorna 200 com os dados do feriado")
        void reuniaoEmFeriado_deveDevolverFeriadoEncontrado() throws Exception {

            when(feriadoService.verificarReuniao(1L))
                    .thenReturn(ReuniaoFeriadoDTO.emFeriado(
                            1L, TITULO, "2026-12-25", "Natal", "sexta-feira"));


            ResultActions resposta = mockMvc.perform(get(URL_FERIADO, 1L));


            resposta.andExpect(status().isOk())
                    .andExpect(jsonPath("$.reuniaoId").value(1))
                    .andExpect(jsonPath("$.feriado").value(true))
                    .andExpect(jsonPath("$.nomeFeriado").value("Natal"))
                    .andExpect(jsonPath("$.diaSemana").value("sexta-feira"));

            verify(feriadoService).verificarReuniao(1L);
        }

        @Test
        @DisplayName("Dado que o service não encontra feriado, quando verificar, então retorna 200 sem corpo")
        void semFeriado_deveRetornarOkSemCorpo() throws Exception {

            when(feriadoService.verificarReuniao(1L)).thenReturn(null);


            ResultActions resposta = mockMvc.perform(get(URL_FERIADO, 1L));


            resposta.andExpect(status().isOk())
                    .andExpect(content().string(""));

            verify(feriadoService).verificarReuniao(1L);
        }

        @Test
        @DisplayName("Dada reunião sem data reconhecível, quando verificar, então retorna 404 com o motivo")
        void reuniaoSemDataValida_deveRetornarNotFound() throws Exception {

            String detalhe = "A reunião 1 não tem data em formato reconhecível (aaaa-MM-dd)";
            when(feriadoService.verificarReuniao(1L))
                    .thenThrow(new RecursoNaoEncontradoException(detalhe));


            ResultActions resposta = mockMvc.perform(get(URL_FERIADO, 1L));


            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail").value(detalhe));
        }
    }

    @Nested
    @DisplayName("Exclusão de reuniões")
    class Exclusao {

        @Test
        @DisplayName("Dado id existente, quando excluir, então retorna 204 sem corpo")
        void idExistente_deveRetornarNoContent() throws Exception {

            doNothing().when(reuniaoService).deletar(1L);


            ResultActions resposta = mockMvc.perform(delete(URL_POR_ID, 1L));


            resposta.andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(reuniaoService).deletar(1L);
        }

        @Test
        @DisplayName("Dado id inexistente, quando excluir, então retorna 404")
        void idInexistente_deveRetornarNotFound() throws Exception {

            doThrow(new RecursoNaoEncontradoException(MENSAGEM_NAO_ENCONTRADA))
                    .when(reuniaoService).deletar(99L);


            ResultActions resposta = mockMvc.perform(delete(URL_POR_ID, 99L));


            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));

            verify(reuniaoService).deletar(99L);
        }
    }
}