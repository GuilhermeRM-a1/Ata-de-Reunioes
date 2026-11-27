package br.com.empresa.reunioes.application.login;

import br.com.empresa.reunioes.application.colaborador.ColaboradorService;
import br.com.empresa.reunioes.application.login.record.Auth;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.enums.Papel;
import br.com.empresa.reunioes.infra.config.security.JwtService;
import br.com.empresa.reunioes.web.colaborador.dto.request.ColaboradorRequest;
import br.com.empresa.reunioes.web.login.dto.LoginResponse;
import br.com.empresa.reunioes.web.login.dto.RegistroRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Cadastro público de colaborador. Reaproveita o ColaboradorService, que já
 * valida e-mail duplicado (409) e grava a senha com BCrypt, e devolve o token
 * para o usuário já entrar logado.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistroService {

    private final ColaboradorService colaboradorService;
    private final JwtService jwtService;

    public Auth registrar(RegistroRequest request) {
        log.info("Iniciando cadastro público de colaborador.");

        ColaboradorRequest novo = new ColaboradorRequest(
                request.nome().trim(),
                request.email().trim(),
                request.senha(),
                Papel.USUARIO,
                false,
                LocalDate.now().toString());

        Colaborador colaborador = colaboradorService.salvar(novo);
        String token = jwtService.gerarToken(colaborador);

        log.info("Cadastro público concluído: id={}", colaborador.getId());

        return new Auth(colaborador, token);
    }
}
