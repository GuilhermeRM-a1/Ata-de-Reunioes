package br.com.empresa.reunioes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class ReunioesApplicationTest {

    @Test
    @DisplayName("Deve carregar o contexto da aplicação")
    void contextLoads() {
    }

}
