package br.com.empresa.reunioes.web.login.mapper;

import br.com.empresa.reunioes.application.login.record.Auth;
import br.com.empresa.reunioes.web.login.dto.LoginResponse;

public final class LoginWebMapper {

    private LoginWebMapper() {}

    public static LoginResponse authToResponse(Auth autenticacao) {
        return new LoginResponse(
                autenticacao.token(),
                autenticacao.colaborador().getNome(),
                autenticacao.colaborador().getEmail(),
                autenticacao.colaborador().getPapel());
    }
}
