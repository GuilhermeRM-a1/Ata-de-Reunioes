package br.com.empresa.reunioes.web.login;

import br.com.empresa.reunioes.application.login.LoginService;
import br.com.empresa.reunioes.application.login.RegistroService;
import br.com.empresa.reunioes.application.login.record.Auth;
import br.com.empresa.reunioes.web.login.dto.LoginRequest;
import br.com.empresa.reunioes.web.login.dto.LoginResponse;
import br.com.empresa.reunioes.web.login.dto.RegistroRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static br.com.empresa.reunioes.web.login.mapper.LoginWebMapper.authToResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Login", description = "Autenticação por email e senha")
public class LoginController {

    private final LoginService loginService;
    private final RegistroService registroService;

    @Operation(summary = "Faz login e devolve o token JWT",
            description = "Rota pública. O token vale 8 horas e deve ir no cabeçalho "
                    + "Authorization como Bearer.")
    @ApiResponse(responseCode = "200", description = "Login realizado")
    @ApiResponse(responseCode = "400", description = "Email ou senha ausentes")
    @ApiResponse(responseCode = "401", description = "Email ou senha incorretos")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        Auth autenticacao = loginService.autenticar(request);
        return ResponseEntity.ok(authToResponse(autenticacao));
    }

    @Operation(summary = "Cadastra um novo usuário e já devolve o token JWT",
            description = "Rota pública. O colaborador é sempre criado com o papel USUARIO.")
    @ApiResponse(responseCode = "201", description = "Usuário cadastrado")
    @ApiResponse(responseCode = "400", description = "Campos ausentes ou inválidos")
    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> registrar(@Valid @RequestBody RegistroRequest request) {

        Auth autenticacao = registroService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(authToResponse(autenticacao));
    }
}