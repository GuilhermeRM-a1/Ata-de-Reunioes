package br.com.empresa.reunioes.infra.config.security;


import br.com.empresa.reunioes.domain.repository.ColaboradorRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ColaboradorRepository colaboradorRepository;

    public SecurityFilter(JwtService jwtService, ColaboradorRepository colaboradorRepository) {
        this.jwtService = jwtService;
        this.colaboradorRepository = colaboradorRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extrairToken(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            autenticar(token);
        }

        filterChain.doFilter(request, response);
    }

    private void autenticar(String token) {
        try {
            String subject = jwtService.validarToken(token); // null se inválido
            if (subject == null) {
                return;
            }

            Long id = Long.valueOf(subject);

            colaboradorRepository.findById(id).ifPresent(colaborador -> {
                var authentication = new UsernamePasswordAuthenticationToken(
                        colaborador, null, colaborador.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        } catch (RuntimeException e) {
            // token expirado, assinatura inválida, subject não numérico etc.
            SecurityContextHolder.clearContext();
        }
    }

    private String extrairToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring("Bearer ".length()).trim();
        return token.isEmpty() ? null : token;
    }
}
