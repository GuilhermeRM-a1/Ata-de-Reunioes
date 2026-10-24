package br.com.empresa.reunioes.infra.client;

import br.com.empresa.reunioes.infra.client.dto.FeriadoClientResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import feign.Feign;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import tools.jackson.databind.ObjectMapper;


import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

class FeriadoClientTest {

    static WireMockServer wireMock = new WireMockServer(wireMockConfig().dynamicPort());
    static FeriadoClient client;
    static ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void configurar() {
        wireMock.start();
        client = Feign.builder()
                .contract(new SpringMvcContract())
                .decoder((response, type) -> mapper.readValue(
                        response.body().asInputStream(),
                        mapper.getTypeFactory().constructType(type)))
                .target(FeriadoClient.class, wireMock.baseUrl() + "/api/feriados/v1");
    }

    @BeforeEach
    void limparStubs() {
        wireMock.resetAll();
    }

    @AfterAll
    static void encerrar() {
        wireMock.stop();
    }

    @Test
    void deveListarFeriadosPorAno() {
        wireMock.stubFor(get(urlPathEqualTo("/api/feriados/v1/2026"))
                .willReturn(okJson("""
                    [{"date":"2026-01-01","name":"Confraternização Universal",
                      "type":"national","weekday":"Thursday"}]
                    """)));

        var resultado = client.listarPorAno(2026);

        assertThat(resultado).containsExactly(new FeriadoClientResponse(
                "2026-01-01", "Confraternização Universal", "national", "Thursday"));
        wireMock.verify(1, getRequestedFor(urlPathEqualTo("/api/feriados/v1/2026")));
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaFeriados() {
        wireMock.stubFor(get(urlPathEqualTo("/api/feriados/v1/2026"))
                .willReturn(okJson("[]")));

        assertThat(client.listarPorAno(2026)).isEmpty();
    }
}