package br.com.empresa.reunioes.infra.config.security;

import br.com.empresa.reunioes.domain.entity.Colaborador;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final String issuer;

    public JwtService(

            // secret ainda nao configurada, lembrar depois
            @Value("${api.security.token.secret}") String secret,
            @Value("${api.security.token.issuer}") String issuer
    ) {

        this.algorithm = Algorithm.HMAC256(secret);
        this.issuer = issuer;
    }

    public String gerarToken(Colaborador colaborador) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(colaborador.getId()))
                .withClaim("id", colaborador.getId())
                .withClaim("papel", colaborador.getPapel().name())
                .withExpiresAt(Instant.now().plus(8, ChronoUnit.HOURS))
                .sign(algorithm);
    }

    public String validarToken(String token) {
        try {
            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException | IllegalArgumentException exception) {
            return null;
        }
    }
}