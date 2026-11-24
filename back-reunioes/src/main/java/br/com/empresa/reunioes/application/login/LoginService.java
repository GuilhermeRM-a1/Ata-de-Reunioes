package br.com.empresa.reunioes.application.login;

import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.infra.config.security.JwtService;
import br.com.empresa.reunioes.web.login.dto.LoginRequest;
import br.com.empresa.reunioes.web.login.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Confere email e senha e, se estiverem certos, emite o token JWT.
 *
 * Quem compara a senha e o AuthenticationManager (BCrypt contra o hash do
 * banco). Se estiver errada, ele lanca AuthenticationException, que o
 * RestExceptionHandler transforma em 401 sem dizer se o erro foi no email
 * ou na senha.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponse autenticar(LoginRequest request) {
        log.info("Tentativa de login para {}.", request.email());

        Authentication autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha()));

        Colaborador colaborador = (Colaborador) autenticacao.getPrincipal();

        String token = jwtService.gerarToken(colaborador);

        log.info("Login concluído para o colaborador {}.", colaborador.getId());

        return new LoginResponse(token, colaborador.getNome(), colaborador.getEmail(), colaborador.getPapel());
    }
}