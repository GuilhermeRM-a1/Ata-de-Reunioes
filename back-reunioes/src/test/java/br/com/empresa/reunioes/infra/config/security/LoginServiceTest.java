package br.com.empresa.reunioes.login;

import br.com.empresa.reunioes.application.login.LoginService;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.enums.Papel;
import br.com.empresa.reunioes.infra.config.security.JwtService;
import br.com.empresa.reunioes.web.login.dto.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtService jwtService;

    @InjectMocks
    LoginService loginService;

    private Colaborador ana() {
        var colaborador = new Colaborador();
        colaborador.setId(1L);
        colaborador.setNome("Ana Beatriz Fontes");
        colaborador.setEmail("ana.fontes@exemplo.com.br");
        colaborador.setSenha("hash");
        colaborador.setPapel(Papel.ADMIN);
        return colaborador;
    }

    @Test
    @DisplayName("Devolve token, nome, email e papel quando as credenciais estão certas")
    void deveAutenticarEGerarToken() {
        var colaborador = ana();
        Authentication autenticada = new UsernamePasswordAuthenticationToken(
                colaborador, null, colaborador.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(autenticada);
        when(jwtService.gerarToken(colaborador)).thenReturn("token-jwt");

        var resposta = loginService.autenticar(
                new LoginRequest("ana.fontes@exemplo.com.br", "senha123"));

        assertThat(resposta.token()).isEqualTo("token-jwt");
        assertThat(resposta.nome()).isEqualTo("Ana Beatriz Fontes");
        assertThat(resposta.email()).isEqualTo("ana.fontes@exemplo.com.br");
        assertThat(resposta.papel()).isEqualTo(Papel.ADMIN);
    }

    @Test
    @DisplayName("Entrega email e senha ao AuthenticationManager")
    void deveEnviarCredenciaisAoManager() {
        var colaborador = ana();
        when(authenticationManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken(colaborador, null, colaborador.getAuthorities()));
        when(jwtService.gerarToken(colaborador)).thenReturn("token-jwt");

        loginService.autenticar(new LoginRequest("ana.fontes@exemplo.com.br", "senha123"));

        var captor = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(captor.capture());

        assertThat(captor.getValue().getPrincipal()).isEqualTo("ana.fontes@exemplo.com.br");
        assertThat(captor.getValue().getCredentials()).isEqualTo("senha123");
    }

    @Test
    @DisplayName("Propaga a falha e não gera token quando a senha está errada")
    void naoDeveGerarTokenComSenhaErrada() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> loginService.autenticar(
                new LoginRequest("ana.fontes@exemplo.com.br", "errada")))
                .isInstanceOf(BadCredentialsException.class);

        verify(jwtService, never()).gerarToken(any());
    }
}