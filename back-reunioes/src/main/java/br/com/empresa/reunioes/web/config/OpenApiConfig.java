package br.com.empresa.reunioes.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Titulo, descricao e versao que aparecem no topo do Swagger UI. */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reunioesOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Atas de Reunião")
                        .description("Gestão de atas de reunião: reuniões, ações e colaboradores. "
                                + "Projeto Integrador do 4º período de ADS.")
                        .version("v1")
                        .contact(new Contact().name("Equipe ReuniõesApp"))
                        .license(new License().name("MIT")));
    }
}
