
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("Controller de reuniões")
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

    @Nested
    @DisplayName("Consultas de reuniões")
    class Consultas {

        @Test
        @DisplayName("Deve retornar uma página de reuniões")
        void listarRetornaPaginaDeReunioes() throws Exception {
            // Given
            when(reuniaoService.listar(any()))
                    .thenReturn(new PageImpl<>(List.of(criarReuniao())));

            // When / Then
            mockMvc.perform(get("/api/reunioes")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(reuniaoService).listar(any());
        }

        @Test
        @DisplayName("Deve retornar uma reunião pelo ID")
        void buscarPorIdRetornaReuniao() throws Exception {
            // Given
            when(reuniaoService.buscarPorId(1L))
                    .thenReturn(criarReuniao());

            // When / Then
            mockMvc.perform(get("/api/reunioes/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.titulo")
                            .value("Reunião de Alinhamento"));

            verify(reuniaoService).buscarPorId(1L);
        }

        @Test
        @DisplayName("Deve retornar 404 quando a reunião não existir")
        void buscarPorIdRetornaNotFoundQuandoReuniaoNaoExiste()
                throws Exception {
            // Given
            when(reuniaoService.buscarPorId(99L))
                    .thenThrow(new RecursoNaoEncontradoException(
                            "Reunião não encontrada"));

            // When / Then
            mockMvc.perform(get("/api/reunioes/99"))
                    .andExpect(status().isNotFound());

            verify(reuniaoService).buscarPorId(99L);
        }
    }

    @Nested
    @DisplayName("Cadastro de reuniões")
    class Cadastro {

        @Test
        @DisplayName("Deve cadastrar reunião e retornar 201 com Location")
        void salvarRetornaCreatedComLocation() throws Exception {
            // Given
            when(reuniaoService.salvar(any()))
                    .thenReturn(criarReuniao());

            // When / Then
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
        @DisplayName("Deve rejeitar cadastro com dados inválidos")
        void salvarRetornaBadRequestQuandoRequestInvalido()
                throws Exception {
            // Given
            var request = requestInvalido();

            // When / Then
            mockMvc.perform(post("/api/reunioes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(request)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(reuniaoService);
        }
    }

    @Nested
    @DisplayName("Atualização de reuniões")
    class Atualizacao {

        @Test
        @DisplayName("Deve atualizar uma reunião")
        void atualizarRetornaReuniaoAtualizada() throws Exception {
            // Given
            when(reuniaoService.atualizar(eq(1L), any()))
                    .thenReturn(criarReuniao());

            // When / Then
            mockMvc.perform(put("/api/reunioes/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(requestValido())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.titulo")
                            .value("Reunião de Alinhamento"));

            verify(reuniaoService).atualizar(eq(1L), any());
        }

        @Test
        @DisplayName("Deve atualizar parcialmente uma reunião")
        void atualizarParcialRetornaReuniaoAtualizada()
                throws Exception {
            // Given
            var request = new ReuniaoPatchRequest(
                    "Novo título", null, null, null,
                    null, null, null, null, null
            );

            when(reuniaoService.atualizarParcial(eq(1L), any()))
                    .thenReturn(criarReuniao());

            // When / Then
            mockMvc.perform(patch("/api/reunioes/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(request)))
                    .andExpect(status().isOk());

            verify(reuniaoService)
                    .atualizarParcial(eq(1L), any());
        }
    }

    @Nested
    @DisplayName("Ingestão de dados da reunião")
    class Ingestao {

        @Test
        @DisplayName("Deve aceitar os dados recebidos para análise")
        void receberAnaliseRetornaAccepted() throws Exception {
            // Given
            var request = new ReuniaoResumoRequest(
                    "Resumo executivo da reunião",
                    "Transcrição completa da reunião"
            );

            when(ingestaoService.receberAnalise(eq(1L), any()))
                    .thenReturn(criarReuniao());

            // When / Then
            mockMvc.perform(patch("/api/reunioes/1/ingestao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(request)))
                    .andExpect(status().isAccepted());

            verify(ingestaoService)
                    .receberAnalise(eq(1L), any());
        }
    }

    @Nested
    @DisplayName("Geração de relatórios")
    class Relatorios {

        @Test
        @DisplayName("Deve retornar o relatório da reunião")
        void gerarRelatorioRetornaRelatorio() throws Exception {
            // Given
            when(relatorioService.validarReuniaoParaRelatorio(1L))
                    .thenReturn(criarReuniao());

            // When / Then
            mockMvc.perform(get("/api/reunioes/1/relatorio"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.titulo")
                            .value("Reunião de Alinhamento"));

            verify(relatorioService)
                    .validarReuniaoParaRelatorio(1L);
        }
    }

    @Nested
    @DisplayName("Verificação de feriados")
    class Feriados {

        @Test
        @DisplayName("Deve retornar sucesso ao verificar feriado")
        void verificarFeriadoRetornaResultado() throws Exception {
            // Given
            when(feriadoService.verificarReuniao(1L))
                    .thenReturn(null);

            // When / Then
            mockMvc.perform(get("/api/reunioes/1/feriado"))
                    .andExpect(status().isOk());

            verify(feriadoService).verificarReuniao(1L);
        }
    }

    @Nested
    @DisplayName("Exclusão de reuniões")
    class Exclusao {

        @Test
        @DisplayName("Deve excluir uma reunião e retornar 204")
        void deletarRetornaNoContent() throws Exception {
            // Given
            doNothing().when(reuniaoService).deletar(1L);

            // When / Then
            mockMvc.perform(delete("/api/reunioes/1"))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(reuniaoService).deletar(1L);
        }
    }
}
