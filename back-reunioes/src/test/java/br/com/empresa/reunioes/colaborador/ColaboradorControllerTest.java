package br.com.empresa.reunioes.colaborador;

import br.com.empresa.reunioes.application.colaborador.ColaboradorService;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.enums.Papel;
import br.com.empresa.reunioes.domain.exception.EmailCadastradoExistenteException;
import br.com.empresa.reunioes.domain.exception.RecursoNaoEncontradoException;
import br.com.empresa.reunioes.web.colaborador.ColaboradorController;
import br.com.empresa.reunioes.web.colaborador.dto.request.ColaboradorPatchRequest;
import br.com.empresa.reunioes.web.colaborador.dto.request.ColaboradorRequest;
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

@WebMvcTest(ColaboradorController.class)
@DisplayName("Controller de colaboradores")
class ColaboradorControllerTest {

    private static final String URL_BASE = "/api/reunioes/colaboradores";
    private static final String URL_POR_ID = URL_BASE + "/{id}";
    private static final String URL_POR_EMAIL = URL_BASE + "/email/{email}";
    private static final String URL_POR_EMAIL_PARAM = URL_BASE + "/email";

    private static final String EMAIL = "maria.souza@empresa.com";
    private static final String MENSAGEM_NAO_ENCONTRADO = "Colaborador não encontrado com ID: 99";
    private static final String MENSAGEM_EMAIL_DUPLICADO = "Este e-mail já existe, cadastre outro.";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ColaboradorService service;

    private ColaboradorRequest requestValido() {
        return new ColaboradorRequest(
                "Maria Souza",
                EMAIL,
                "senha123",
                Papel.USUARIO,
                true,
                "2026-02-01"
        );
    }

    private ColaboradorRequest requestInvalido() {
        return new ColaboradorRequest(
                "",
                "nao-e-um-email",
                "",
                Papel.USUARIO,
                true,
                "2026-02-01"
        );
    }

    private Colaborador criarColaborador() {
        var colaborador = new Colaborador();
        colaborador.setId(1L);
        colaborador.setNome("Maria Souza");
        colaborador.setEmail(EMAIL);
        colaborador.setSenha("hash-bcrypt");
        colaborador.setPapel(Papel.USUARIO);
        colaborador.setMonitorarReunioes(true);
        colaborador.setDataCadastro("2026-02-01");
        return colaborador;
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
    @DisplayName("Cadastro de colaboradores")
    class Cadastro {

        @Test
        @DisplayName("Dada requisição válida, quando cadastrar, então retorna 201 com os dados e entrega a requisição ao service")
        void requisicaoValida_deveRetornarCreated() throws Exception {
            when(service.salvar(any())).thenReturn(criarColaborador());

            ResultActions resposta = enviarJson(post(URL_BASE), requestValido());

            resposta.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Maria Souza"))
                    .andExpect(jsonPath("$.email").value(EMAIL))
                    .andExpect(jsonPath("$.senha").doesNotExist());

            ArgumentCaptor<ColaboradorRequest> captor = ArgumentCaptor.forClass(ColaboradorRequest.class);
            verify(service).salvar(captor.capture());
            assertThat(captor.getValue().nome()).isEqualTo("Maria Souza");
            assertThat(captor.getValue().email()).isEqualTo(EMAIL);
        }

        @Test
        @DisplayName("Dada requisição inválida, quando cadastrar, então retorna 400 com os campos inválidos e não chama o service")
        void requisicaoInvalida_deveRetornarBadRequest() throws Exception {
            ResultActions resposta = enviarJson(post(URL_BASE), requestInvalido());

            resposta.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Campos inválidos"))
                    .andExpect(jsonPath("$.campos").isArray());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("Dado e-mail já cadastrado, quando cadastrar, então retorna 409 com a mensagem")
        void emailJaCadastrado_deveRetornarConflict() throws Exception {
            when(service.salvar(any()))
                    .thenThrow(new EmailCadastradoExistenteException(MENSAGEM_EMAIL_DUPLICADO));

            ResultActions resposta = enviarJson(post(URL_BASE), requestValido());

            resposta.andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.detail").value(MENSAGEM_EMAIL_DUPLICADO));
        }
    }

    @Nested
    @DisplayName("Consultas de colaboradores")
    class Consultas {

        @Test
        @DisplayName("Dado id existente, quando buscar, então retorna 200 com os dados do colaborador")
        void idExistente_deveRetornarColaborador() throws Exception {
            when(service.buscarPorId(1L)).thenReturn(criarColaborador());

            ResultActions resposta = mockMvc.perform(get(URL_POR_ID, 1L));

            resposta.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.email").value(EMAIL))
                    .andExpect(jsonPath("$.senha").doesNotExist());

            verify(service).buscarPorId(1L);
        }

        @Test
        @DisplayName("Dado id inexistente, quando buscar, então retorna 404 com a mensagem")
        void idInexistente_deveRetornarNotFound() throws Exception {
            when(service.buscarPorId(99L))
                    .thenThrow(new RecursoNaoEncontradoException(MENSAGEM_NAO_ENCONTRADO));

            ResultActions resposta = mockMvc.perform(get(URL_POR_ID, 99L));

            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.detail").value(MENSAGEM_NAO_ENCONTRADO));
        }

        @Test
        @DisplayName("Dado e-mail cadastrado, quando buscar pelo caminho, então retorna 200 com o colaborador")
        void emailNoCaminho_deveRetornarColaborador() throws Exception {
            when(service.buscarPorEmail(EMAIL)).thenReturn(criarColaborador());

            ResultActions resposta = mockMvc.perform(get(URL_POR_EMAIL, EMAIL));

            resposta.andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(EMAIL));

            verify(service).buscarPorEmail(EMAIL);
        }

        @Test
        @DisplayName("Dado e-mail não cadastrado, quando buscar, então retorna 404")
        void emailNaoCadastrado_deveRetornarNotFound() throws Exception {
            when(service.buscarPorEmail("ninguem@empresa.com"))
                    .thenThrow(new RecursoNaoEncontradoException(
                            "Colaborador com e-mail ninguem@empresa.com não encontrado"));

            ResultActions resposta = mockMvc.perform(get(URL_POR_EMAIL, "ninguem@empresa.com"));

            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Dada paginação, quando listar, então retorna 200 com a página e repassa page e size ao service")
        void paginacao_deveRetornarPaginaDeColaboradores() throws Exception {
            when(service.listar(any()))
                    .thenReturn(new PageImpl<>(List.of(criarColaborador())));

            ResultActions resposta = mockMvc.perform(get(URL_BASE)
                    .param("page", "0")
                    .param("size", "10"));

            resposta.andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].email").value(EMAIL))
                    .andExpect(jsonPath("$.totalElements").value(1));

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(service).listar(captor.capture());
            assertThat(captor.getValue().getPageNumber()).isZero();
            assertThat(captor.getValue().getPageSize()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("Atualização de colaboradores")
    class Atualizacao {

        @Test
        @DisplayName("Dada requisição válida, quando atualizar, então retorna 200 e entrega a requisição ao service")
        void requisicaoValida_deveRetornarColaboradorAtualizado() throws Exception {
            when(service.atualizar(eq(1L), any())).thenReturn(criarColaborador());

            ResultActions resposta = enviarJson(put(URL_POR_ID, 1L), requestValido());

            resposta.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Maria Souza"));

            ArgumentCaptor<ColaboradorRequest> captor = ArgumentCaptor.forClass(ColaboradorRequest.class);
            verify(service).atualizar(eq(1L), captor.capture());
            assertThat(captor.getValue().email()).isEqualTo(EMAIL);
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
                    .thenThrow(new RecursoNaoEncontradoException(MENSAGEM_NAO_ENCONTRADO));

            ResultActions resposta = enviarJson(put(URL_POR_ID, 99L), requestValido());

            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Dado e-mail de outro colaborador, quando atualizar, então retorna 409")
        void emailDeOutroColaborador_deveRetornarConflict() throws Exception {
            when(service.atualizar(eq(1L), any()))
                    .thenThrow(new EmailCadastradoExistenteException(MENSAGEM_EMAIL_DUPLICADO));

            ResultActions resposta = enviarJson(put(URL_POR_ID, 1L), requestValido());

            resposta.andExpect(status().isConflict())
                    .andExpect(jsonPath("$.detail").value(MENSAGEM_EMAIL_DUPLICADO));
        }

        @Test
        @DisplayName("Dado apenas o nome, quando atualizar parcialmente, então retorna 200 e entrega só o nome ao service")
        void apenasNome_deveRetornarColaboradorAtualizado() throws Exception {
            var request = new ColaboradorPatchRequest("Nome Alterado", null, null, null, null, null);
            when(service.atualizarParcial(eq(1L), any())).thenReturn(criarColaborador());

            ResultActions resposta = enviarJson(patch(URL_POR_ID, 1L), request);

            resposta.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));

            ArgumentCaptor<ColaboradorPatchRequest> captor = ArgumentCaptor.forClass(ColaboradorPatchRequest.class);
            verify(service).atualizarParcial(eq(1L), captor.capture());
            assertThat(captor.getValue().nome()).isEqualTo("Nome Alterado");
            assertThat(captor.getValue().email()).isNull();
        }

        @Test
        @DisplayName("Dado nome vazio, quando atualizar parcialmente, então retorna 400 e não chama o service")
        void nomeVazio_aoAtualizarParcialmente_deveRetornarBadRequest() throws Exception {
            var request = new ColaboradorPatchRequest("", null, null, null, null, null);

            ResultActions resposta = enviarJson(patch(URL_POR_ID, 1L), request);

            resposta.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Campos inválidos"));

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("Dado id inexistente, quando atualizar parcialmente, então retorna 404")
        void idInexistente_aoAtualizarParcialmente_deveRetornarNotFound() throws Exception {
            var request = new ColaboradorPatchRequest("Nome Alterado", null, null, null, null, null);
            when(service.atualizarParcial(eq(99L), any()))
                    .thenThrow(new RecursoNaoEncontradoException(MENSAGEM_NAO_ENCONTRADO));

            ResultActions resposta = enviarJson(patch(URL_POR_ID, 99L), request);

            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Dado e-mail de outro colaborador, quando atualizar parcialmente, então retorna 409")
        void emailDeOutroColaborador_aoAtualizarParcialmente_deveRetornarConflict() throws Exception {
            var request = new ColaboradorPatchRequest(null, EMAIL, null, null, null, null);
            when(service.atualizarParcial(eq(1L), any()))
                    .thenThrow(new EmailCadastradoExistenteException(MENSAGEM_EMAIL_DUPLICADO));

            ResultActions resposta = enviarJson(patch(URL_POR_ID, 1L), request);

            resposta.andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409));
        }
    }

    @Nested
    @DisplayName("Exclusão de colaboradores")
    class Exclusao {

        @Test
        @DisplayName("Dado id existente, quando excluir, então retorna 204 sem corpo")
        void idExistente_deveRetornarNoContent() throws Exception {
            doNothing().when(service).deletar(1L);

            ResultActions resposta = mockMvc.perform(delete(URL_POR_ID, 1L));

            resposta.andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(service).deletar(1L);
        }

        @Test
        @DisplayName("Dado id inexistente, quando excluir, então retorna 404")
        void idInexistente_deveRetornarNotFound() throws Exception {
            doThrow(new RecursoNaoEncontradoException(MENSAGEM_NAO_ENCONTRADO))
                    .when(service).deletar(99L);

            ResultActions resposta = mockMvc.perform(delete(URL_POR_ID, 99L));

            resposta.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));

            verify(service).deletar(99L);
        }
    }
}