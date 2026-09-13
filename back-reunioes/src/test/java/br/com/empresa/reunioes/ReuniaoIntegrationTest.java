package br.com.empresa.reunioes;

import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integracao de ponta a ponta da reuniao: HTTP -> service -> Postgres de
 * verdade, subido pelo Testcontainers. Nao usa H2 de proposito — banco
 * diferente do de producao esconde justamente o erro que o teste procura.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class ReuniaoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AcaoRepository acaoRepository;

    /** Instanciado na mao: o contexto nao publica um ObjectMapper. */
    private final ObjectMapper json = new ObjectMapper();

    // ------------------------------------------------------------- 1. criar

    @Test
    @DisplayName("POST cria a reuniao, devolve 201 e o header Location")
    void postCriaReuniao() throws Exception {

        MvcResult resposta = mockMvc.perform(post("/api/reunioes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoValido("Reuniao de planejamento")))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.titulo").value("Reuniao de planejamento"))
                .andReturn();

        // O Location tem de apontar para o recurso recem-criado.
        long id = idDe(resposta);
        assertThat(resposta.getResponse().getHeader("Location"))
                .endsWith("/api/reunioes/" + id);
    }

    // -------------------------------------------------------------- 2. ler

    @Test
    @DisplayName("GET por id devolve exatamente o que foi criado")
    void getPorIdDevolveOqueFoiCriado() throws Exception {

        MvcResult criada = mockMvc.perform(post("/api/reunioes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoValido("Retrospectiva da sprint")))
                .andExpect(status().isCreated())
                .andReturn();

        mockMvc.perform(get("/api/reunioes/{id}", idDe(criada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Retrospectiva da sprint"))
                .andExpect(jsonPath("$.data").value("2026-09-01"))
                .andExpect(jsonPath("$.status").value("CONCLUIDA"))
                .andExpect(jsonPath("$.areas[0]").value("Engenharia"));
    }

    // ---------------------------------------------------------- 3. invalido

    @Test
    @DisplayName("POST invalido devolve 400 no formato padronizado")
    void postInvalidoDevolve400Padronizado() throws Exception {

        // Titulo vazio: reprovado por @NotBlank e por @Size(min = 5).
        String corpo = """
                {"titulo": "", "data": "2026-09-01", "status": "CONCLUIDA"}
                """;

        mockMvc.perform(post("/api/reunioes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Campos inválidos"))
                .andExpect(jsonPath("$.campos[0].campo").exists());
    }

    // --------------------------------------------------------- 4. excluir

    @Test
    @DisplayName("DELETE devolve 204 e o GET seguinte devolve 404")
    void deleteRemoveEDepoisDa404() throws Exception {

        MvcResult criada = mockMvc.perform(post("/api/reunioes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoValido("Reuniao que sera excluida")))
                .andExpect(status().isCreated())
                .andReturn();

        long id = idDe(criada);

        mockMvc.perform(delete("/api/reunioes/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/reunioes/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ------------------------------------------------- 5. ida e volta acoes

    @Test
    @DisplayName("A reuniao guarda as acoes ligadas e as devolve ao reler")
    void reuniaoPreservaAsAcoes() throws Exception {

        long acaoUm = criarAcao("Enviar a proposta");
        long acaoDois = criarAcao("Marcar o follow-up");

        String corpo = """
                {"titulo": "Reuniao com acoes",
                 "data": "2026-09-01",
                 "status": "CONCLUIDA",
                 "areas": ["Engenharia"],
                 "pontosChaves": ["Prazo apertado"],
                 "acoes": [%d, %d]}
                """.formatted(acaoUm, acaoDois);

        MvcResult criada = mockMvc.perform(post("/api/reunioes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalAcoes").value(2))
                .andReturn();

        // A releitura tem de trazer as duas acoes, nao so a contagem do POST.
        mockMvc.perform(get("/api/reunioes/{id}", idDe(criada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAcoes").value(2));

        // E cada acao tem de saber a que reuniao pertence — o vinculo precisa
        // ter ido para o banco, nao ficado so na memoria do service.
        Acao acaoRelida = acaoRepository.findById(acaoUm).orElseThrow();
        assertThat(acaoRelida.getReuniao()).isNotNull();
        assertThat(acaoRelida.getReuniao().getId()).isEqualTo(idDe(criada));
    }

    // ------------------------------------------------------------ apoio

    private String corpoValido(String titulo) {
        return """
                {"titulo": "%s",
                 "data": "2026-09-01",
                 "status": "CONCLUIDA",
                 "areas": ["Engenharia"],
                 "pontosChaves": ["Definir o escopo"]}
                """.formatted(titulo);
    }

    /**
     * Grava direto pelo repositorio, e nao por POST /api/reunioes/acoes.
     * Motivo: AcaoRequest marca @NotBlank num Long, e o Bean Validation nao
     * tem validador para esse par — a requisicao morre em 500 antes de chegar
     * ao service. O conserto e do dono daquela rota; aqui o que se testa e o
     * vinculo entre reuniao e acao, que nao depende do endpoint quebrado.
     */
    private long criarAcao(String titulo) {

        Acao acao = new Acao();
        acao.setTitulo(titulo);
        acao.setDescricao("descricao");
        acao.setTipo("TAREFA");
        acao.setPrazo("2026-09-10");

        return acaoRepository.save(acao).getId();
    }

    private long idDe(MvcResult resposta) throws Exception {
        JsonNode corpo = json.readTree(resposta.getResponse().getContentAsString());
        return corpo.get("id").asLong();
    }

}
