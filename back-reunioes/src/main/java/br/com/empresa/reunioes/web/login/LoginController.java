package br.com.empresa.reunioes.web.login;

import br.com.empresa.reunioes.application.login.LoginService;
import br.com.empresa.reunioes.web.login.dto.LoginRequest;
import br.com.empresa.reunioes.web.login.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Login", description = "Autenticação por email e senha")
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "Faz login e devolve o token JWT",
            description = "Rota pública. O token vale 8 horas e deve ir no cabeçalho "
                    + "Authorization como Bearer.")
    @ApiResponse(responseCode = "200", description = "Login realizado")
    @ApiResponse(responseCode = "400", description = "Email ou senha ausentes")
    @ApiResponse(responseCode = "401", description = "Email ou senha incorretos")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(loginService.autenticar(request));
    }
}