package br.com.empresa.reunioes.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SpringJUnitConfig mapeia qual config utilizar
 * Executa o metodo mapeado com Bean
 * Autowired indica aonde injetar o resultado da execução
 */
@SpringJUnitConfig(OpenApiConfig.class)
@DisplayName("Configuração da documentação OpenAPI")
class OpenApiConfigTest {

    @Autowired
    OpenAPI openAPI;

    @Test
    @DisplayName("Deve registrar o bean OpenAPI")
    void deveRegistrarOpenAPI() {
        assertThat(openAPI).isNotNull();
    }

    @Test
    @DisplayName("Deve configurar os metadados da documentação")
    void deveConfigurarMetadados() {
        assertThat(openAPI.getInfo().getTitle())
                .isEqualTo("API de Atas de Reunião");

        assertThat(openAPI.getInfo().getDescription())
                .isEqualTo(
                        "Gestão de atas de reunião: reuniões, ações e colaboradores. "
                                + "Projeto Integrador do 4º período de ADS."
                );

        assertThat(openAPI.getInfo().getVersion())
                .isEqualTo("v1");
    }

    @Test
    @DisplayName("Deve configurar contato e licença")
    void deveConfigurarContatoELicenca() {
        assertThat(openAPI.getInfo().getContact().getName())
                .isEqualTo("Equipe ReuniõesApp");

        assertThat(openAPI.getInfo().getLicense().getName())
                .isEqualTo("MIT");
    }
}
