package br.com.empresa.reunioes.acao;

import br.com.empresa.reunioes.application.acao.AcaoService;
import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.exception.RecursoNaoEncontradoException;
import br.com.empresa.reunioes.web.acao.AcaoController;
import br.com.empresa.reunioes.web.acao.dto.request.AcaoRequest;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AcaoController.class)
@DisplayName("Controller de ações")
class AcaoControllerTest {

    private static final String URL_BASE = "/api/reunioes/acoes";
    private static final String URL_POR_ID = URL_BASE + "/{id}";
    private static final String URL_POR_REUNIAO = URL_BASE + "/reuniao";
    private static final String URL_EXCLUSAO = URL_BASE + "/{reuniaoId}/{id}";

    private static final String MENSAGEM_ACAO_NAO_ENCONTRADA = "Ação não encontrada";
    private static final String MENSAGEM_REUNIAO_NAO_ENCONTRADA = "Reunião não encontrada.";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AcaoService service;

    private AcaoRequest requestValido() {
        return new AcaoRequest(
                "Revisar código",
                "Revisar o PR do módulo de relatórios",
                "Tecnica",
                "2026-10-20",
                false,
                List.of(1L)
        );
    }

    private AcaoRequest requestInvalido() {
        return new AcaoRequest("", "", "", "", false, List.of());
    }

    private Acao criarAcao() {
        var reuniao = new Reuniao();
        reuniao.setId(1L);

        var acao = new Acao();
        acao.setId(1L);
        acao.setTitulo("Revisar código");
        acao.setDescricao("Revisar o PR do módulo de relatórios");
        acao.setTipo("Tecnica");
        acao.setPrazo("2026-10-20");
        acao.setConcluida(false);
        acao.setResponsavel(new ArrayList<>());
        acao.setReuniao(reuniao);
        return acao;
    }

    private String json(Object objeto) {
        return objectMapper.writeValueAsString(objeto);
    }

    private ResultActions enviarJson(MockHttpServletRequestBuilder metodo, Object corpo) throws Exception {
        return mockMvc.perform(metodo
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(corpo)));
    }

    @Nested
    @DisplayName("Cadastro de ações")
    class Cadastro {

        @Test
        @DisplayName("Dada requisição válida, quando cadastrar, então retorna 201 com os dados e entrega a requisição ao service")
        void requisicaoValida_deveRetornarCreated() throws Exception {
            when(service.salvar(eq(1L), any())).thenReturn(criarAcao());

            ResultActions resposta = enviarJson(post(URL_POR_ID, 1L), requestValido());

            resposta.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.titulo").value("Revisar código"));

            ArgumentCaptor<AcaoRequest> captor = ArgumentCaptor.forClass(AcaoRequest.class);
            verify(service).salvar(eq(1L), captor.capture());
            assertThat(captor.getValue().titulo()).isEqualTo("Revisar código");
            assertThat(captor.getValue().responsavel()).containsExactly(1L);
        }

        @Test
        @DisplayName("Dada requisição inválida, quando cadastrar, então retorna 400 com os campos inválidos e não chama o service")
        void requisicaoInvalida_deveRetornarBadRequest() throws Exception {
            ResultActions resposta = enviarJson(post(URL_POR_ID, 1L), requestInvalido());

            resposta.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Campos inválidos"))
                    .andExpect(jsonPath("$.campos").isArray());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("Dada reunião ou responsável inexistente, quando cadastrar, então retorna 404")
        void reuniaoOuResponsavelInexistente_deveRetornarNotFound() throws Exception {
            when(service.salvar(eq(99L), any()))
                    .thenThrow(new RecursoNaoEncontradoException("Reunião não encontrada"));

            ResultActions resposta = enviarJson(post(URL_POR_ID, 99L), requestValido());

            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("Consultas de ações")
    class Consultas {

        @Test
        @DisplayName("Dada paginação, quando listar, então retorna 200 com a página e repassa page e size ao service")
        void paginacao_deveRetornarPaginaDeAcoes() throws Exception {
            when(service.listar(any())).thenReturn(new PageImpl<>(List.of(criarAcao())));

            ResultActions resposta = mockMvc.perform(get(URL_BASE)
                    .param("page", "0")
                    .param("size", "10"));

            resposta.andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(1))
                    .andExpect(jsonPath("$.totalElements").value(1));

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(service).listar(captor.capture());
            assertThat(captor.getValue().getPageNumber()).isZero();
            assertThat(captor.getValue().getPageSize()).isEqualTo(10);
        }

        @Test
        @DisplayName("Dada reunião existente, quando listar por reunião, então retorna 200 com as ações")
        void reuniaoExistente_deveRetornarAcoesDaReuniao() throws Exception {
            when(service.listarPorReuniao(1L)).thenReturn(List.of(criarAcao()));

            ResultActions resposta = mockMvc.perform(get(URL_POR_REUNIAO).param("reuniaoId", "1"));

            resposta.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(1));

            verify(service).listarPorReuniao(1L);
        }

        @Test
        @DisplayName("Dada reunião inexistente, quando listar por reunião, então retorna 404")
        void reuniaoInexistente_deveRetornarNotFound() throws Exception {
            when(service.listarPorReuniao(99L))
                    .thenThrow(new RecursoNaoEncontradoException(MENSAGEM_REUNIAO_NAO_ENCONTRADA));

            ResultActions resposta = mockMvc.perform(get(URL_POR_REUNIAO).param("reuniaoId", "99"));

            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail").value(MENSAGEM_REUNIAO_NAO_ENCONTRADA));
        }

        @Test
        @DisplayName("Dado id existente, quando buscar, então retorna 200 com os dados da ação")
        void idExistente_deveRetornarAcao() throws Exception {
            when(service.buscarPorId(1L)).thenReturn(criarAcao());

            ResultActions resposta = mockMvc.perform(get(URL_POR_ID, 1L));

            resposta.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.titulo").value("Revisar código"));

            verify(service).buscarPorId(1L);
        }

        @Test
        @DisplayName("Dado id inexistente, quando buscar, então retorna 404 com a mensagem")
        void idInexistente_deveRetornarNotFound() throws Exception {
            when(service.buscarPorId(99L))
                    .thenThrow(new RecursoNaoEncontradoException(MENSAGEM_ACAO_NAO_ENCONTRADA));

            ResultActions resposta = mockMvc.perform(get(URL_POR_ID, 99L));

            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.detail").value(MENSAGEM_ACAO_NAO_ENCONTRADA));
        }

        @Test
        @DisplayName("Dado id em formato inválido, quando buscar, então retorna 400 e não chama o service")
        void idEmFormatoInvalido_deveRetornarBadRequest() throws Exception {
            ResultActions resposta = mockMvc.perform(get(URL_POR_ID, "abc"));

            resposta.andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("Atualização de ações")
    class Atualizacao {

        @Test
        @DisplayName("Dada requisição válida, quando atualizar, então retorna 200 e entrega a requisição ao service")
        void requisicaoValida_deveRetornarAcaoAtualizada() throws Exception {
            when(service.atualizar(eq(1L), any())).thenReturn(criarAcao());

            ResultActions resposta = enviarJson(put(URL_POR_ID, 1L), requestValido());

            resposta.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));

            ArgumentCaptor<AcaoRequest> captor = ArgumentCaptor.forClass(AcaoRequest.class);
            verify(service).atualizar(eq(1L), captor.capture());
            assertThat(captor.getValue().tipo()).isEqualTo("Tecnica");
        }

        @Test
        @DisplayName("Dada requisição inválida, quando atualizar, então retorna 400 e não chama o service")
        void requisicaoInvalida_deveRetornarBadRequest() throws Exception {
            ResultActions resposta = enviarJson(put(URL_POR_ID, 1L), requestInvalido());

            resposta.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Campos inválidos"));

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("Dado id inexistente, quando atualizar, então retorna 404")
        void idInexistente_deveRetornarNotFound() throws Exception {
            when(service.atualizar(eq(99L), any()))
                    .thenThrow(new RecursoNaoEncontradoException(MENSAGEM_ACAO_NAO_ENCONTRADA));

            ResultActions resposta = enviarJson(put(URL_POR_ID, 99L), requestValido());

            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Dado apenas o título, quando atualizar parcialmente, então retorna 200 e entrega só o título ao service")
        void apenasTitulo_deveRetornarAcaoAtualizada() throws Exception {
            when(service.atualizarParcial(eq(1L), any())).thenReturn(criarAcao());

            ResultActions resposta = mockMvc.perform(patch(URL_POR_ID, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"titulo\":\"Título novo\"}"));

            resposta.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));

            ArgumentCaptor<AcaoRequest> captor = ArgumentCaptor.forClass(AcaoRequest.class);
            verify(service).atualizarParcial(eq(1L), captor.capture());
            assertThat(captor.getValue().titulo()).isEqualTo("Título novo");
            assertThat(captor.getValue().descricao()).isNull();
        }

        @Test
        @DisplayName("Dado id inexistente, quando atualizar parcialmente, então retorna 404")
        void idInexistente_aoAtualizarParcialmente_deveRetornarNotFound() throws Exception {
            when(service.atualizarParcial(eq(99L), any()))
                    .thenThrow(new RecursoNaoEncontradoException(MENSAGEM_ACAO_NAO_ENCONTRADA));

            ResultActions resposta = mockMvc.perform(patch(URL_POR_ID, 99L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"titulo\":\"Título novo\"}"));

            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("Exclusão de ações")
    class Exclusao {

        @Test
        @DisplayName("Dado id existente, quando excluir, então retorna 204 sem corpo e usa o id da ação, não o da reunião")
        void idExistente_deveRetornarNoContent() throws Exception {
            doNothing().when(service).deletar(5L);

            ResultActions resposta = mockMvc.perform(delete(URL_EXCLUSAO, 1L, 5L));

            resposta.andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(service).deletar(5L);
        }

        @Test
        @DisplayName("Dado id inexistente, quando excluir, então retorna 404")
        void idInexistente_deveRetornarNotFound() throws Exception {
            doThrow(new RecursoNaoEncontradoException(MENSAGEM_ACAO_NAO_ENCONTRADA))
                    .when(service).deletar(99L);

            ResultActions resposta = mockMvc.perform(delete(URL_EXCLUSAO, 1L, 99L));

            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }
}
