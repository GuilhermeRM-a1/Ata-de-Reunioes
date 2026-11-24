package br.com.empresa.reunioes.infra.config.security;

import br.com.empresa.reunioes.application.login.ColaboradorDetailsService;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.enums.Papel;
import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColaboradorDetailsServiceTest {

    @Mock
    ColaboradorRepository colaboradorRepository;

    @InjectMocks
    ColaboradorDetailsService service;

    @Test
    @DisplayName("Devolve o próprio colaborador quando o email existe")
    void deveDevolverColaboradorPeloEmail() {
        var colaborador = new Colaborador();
        colaborador.setEmail("ana.fontes@exemplo.com.br");
        colaborador.setSenha("hash");
        colaborador.setPapel(Papel.ADMIN);

        when(colaboradorRepository.findByEmail("ana.fontes@exemplo.com.br")).thenReturn(colaborador);

        UserDetails resultado = service.loadUserByUsername("ana.fontes@exemplo.com.br");

        assertThat(resultado).isSameAs(colaborador);
        assertThat(resultado.getUsername()).isEqualTo("ana.fontes@exemplo.com.br");
        assertThat(resultado.getAuthorities()).extracting("authority").containsExactly("ADMIN");
    }

    @Test
    @DisplayName("Lança UsernameNotFoundException quando o email não existe")
    void deveLancarQuandoEmailNaoExiste() {
        when(colaboradorRepository.findByEmail("ninguem@exemplo.com.br")).thenReturn(null);

        assertThatThrownBy(() -> service.loadUserByUsername("ninguem@exemplo.com.br"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}