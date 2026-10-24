package br.com.empresa.reunioes;

import br.com.empresa.reunioes.application.reuniao.ReuniaoService;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ReunioesApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("Deve carregar o contexto da aplicação")
    void contextLoads() {
        assertThat(context.getBean(ReuniaoService.class)).isNotNull();   }
}
