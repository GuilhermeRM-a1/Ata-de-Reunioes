package br.com.empresa.reunioes.reuniao;

import br.com.empresa.reunioes.application.reuniao.FeriadoService;
import br.com.empresa.reunioes.application.reuniao.IngestaoService;
import br.com.empresa.reunioes.application.reuniao.RelatorioService;
import br.com.empresa.reunioes.application.reuniao.ReuniaoService;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.enums.StatusReuniao;
import br.com.empresa.reunioes.domain.enums.StatusTranscricao;
import br.com.empresa.reunioes.domain.exception.RecursoNaoEncontradoException;
import br.com.empresa.reunioes.web.reuniao.ReuniaoController;
import br.com.empresa.reunioes.web.reuniao.dto.ReuniaoPatchRequest;
import br.com.empresa.reunioes.web.reuniao.dto.ReuniaoRequest;
import br.com.empresa.reunioes.web.reuniao.dto.ReuniaoResumoRequest;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReuniaoController.class)
class ReuniaoControllerTest {

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
        reuniao.setTitulo("Reunião de Alinhamento");
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
                "Reunião de Alinhamento",
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

    private String json(Object objeto) throws Exception {
        return objectMapper.writeValueAsString(objeto);
    }

    @Test
    void listarRetornaPaginaDeReunioes() throws Exception {
        when(reuniaoService.listar(any()))
                .thenReturn(new PageImpl<>(List.of(criarReuniao())));

        mockMvc.perform(get("/api/reunioes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(reuniaoService).listar(any());
    }

    @Test
    void buscarPorIdRetornaReuniao() throws Exception {
        when(reuniaoService.buscarPorId(1L))
                .thenReturn(criarReuniao());

        mockMvc.perform(get("/api/reunioes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo")
                        .value("Reunião de Alinhamento"));

        verify(reuniaoService).buscarPorId(1L);
    }

    @Test
    void buscarPorIdRetornaNotFoundQuandoReuniaoNaoExiste()
            throws Exception {
        when(reuniaoService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException(
                        "Reunião não encontrada"));

        mockMvc.perform(get("/api/reunioes/99"))
                .andExpect(status().isNotFound());

        verify(reuniaoService).buscarPorId(99L);
    }

    @Test
    void salvarRetornaCreatedComLocation() throws Exception {
        when(reuniaoService.salvar(any()))
                .thenReturn(criarReuniao());

        mockMvc.perform(post("/api/reunioes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requestValido())))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        org.hamcrest.Matchers.endsWith(
                                "/api/reunioes/1")))
                .andExpect(jsonPath("$.titulo")
                        .value("Reunião de Alinhamento"));

        verify(reuniaoService).salvar(any());
    }

    @Test
    void salvarRetornaBadRequestQuandoRequestInvalido()
            throws Exception {
        mockMvc.perform(post("/api/reunioes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requestInvalido())))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reuniaoService);
    }

    @Test
    void atualizarRetornaReuniaoAtualizada() throws Exception {
        when(reuniaoService.atualizar(eq(1L), any()))
                .thenReturn(criarReuniao());

        mockMvc.perform(put("/api/reunioes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requestValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo")
                        .value("Reunião de Alinhamento"));

        verify(reuniaoService).atualizar(eq(1L), any());
    }

    @Test
    void atualizarParcialRetornaReuniaoAtualizada() throws Exception {
        Reuniao reuniao = criarReuniao();
        ReuniaoPatchRequest request = new ReuniaoPatchRequest(
                "Novo título", null, null, null,
                null, null, null, null, null
        );

        when(reuniaoService.atualizarParcial(eq(1L), any()))
                .thenReturn(reuniao);

        mockMvc.perform(patch("/api/reunioes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk());

        verify(reuniaoService).atualizarParcial(eq(1L), any());
    }

    @Test
    void receberAnaliseRetornaAccepted() throws Exception {
        ReuniaoResumoRequest request = new ReuniaoResumoRequest(
                "Resumo executivo da reunião",
                "Transcrição completa da reunião"
        );

        when(ingestaoService.receberAnalise(eq(1L), any()))
                .thenReturn(criarReuniao());

        mockMvc.perform(patch("/api/reunioes/1/ingestao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isAccepted());

        verify(ingestaoService).receberAnalise(eq(1L), any());
    }

    @Test
    void gerarRelatorioRetornaRelatorio() throws Exception {
        when(relatorioService.validarReuniaoParaRelatorio(1L))
                .thenReturn(criarReuniao());

        mockMvc.perform(get("/api/reunioes/1/relatorio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo")
                        .value("Reunião de Alinhamento"));

        verify(relatorioService)
                .validarReuniaoParaRelatorio(1L);
    }

    @Test
    void verificarFeriadoRetornaResultado() throws Exception {
        when(feriadoService.verificarReuniao(1L))
                .thenReturn(null);

        mockMvc.perform(get("/api/reunioes/1/feriado"))
                .andExpect(status().isOk());

        verify(feriadoService).verificarReuniao(1L);
    }

    @Test
    void deletarRetornaNoContent() throws Exception {
        mockMvc.perform(delete("/api/reunioes/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(reuniaoService).deletar(1L);
    }
}