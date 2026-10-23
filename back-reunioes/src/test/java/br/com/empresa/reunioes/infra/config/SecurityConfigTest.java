package br.com.empresa.reunioes.infra.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SpringJUnitConfig mapeia qual config utilizar
 * Executa o metodo mapeado com Bean
 * Autowired indica aonde injetar o resultado da execução
 */
@SpringJUnitConfig(SecurityConfig.class)
@DisplayName("Configuração de segurança")
class SecurityConfigTest {

    @Autowired
    BCryptPasswordEncoder encoderConfig;

    @Nested
    @DisplayName("Validação e codificação de senha")
    class Senha{
        @Test
        @DisplayName("Deve instanciar e retornar PasswordEncoder")
        void deveInstanciarPasswordEncoder () {
            assertThat(encoderConfig)
                    .isInstanceOf(BCryptPasswordEncoder.class);
        }

        @Test
        @DisplayName("Deve codificar a senha e valida-lá, estando correta ou incorreta")
        void deveCodificarEValidarSenha () {
            String senha = "senha123";

            String senhaHash = encoderConfig.encode(senha);

            assertThat(senhaHash).isNotEqualTo(senha);
            assertThat(encoderConfig.matches(senha, senhaHash)).isTrue();
            assertThat(encoderConfig.matches("senhaErrada", senhaHash)).isFalse();
        }
    }
}
