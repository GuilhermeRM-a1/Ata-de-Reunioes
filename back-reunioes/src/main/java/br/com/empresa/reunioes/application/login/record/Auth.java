package br.com.empresa.reunioes.application.login.record;

import br.com.empresa.reunioes.domain.entity.Colaborador;

public record Auth(Colaborador colaborador, String token) {}
