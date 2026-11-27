package br.com.empresa.reunioes.web.login.dto;

import br.com.empresa.reunioes.domain.enums.Papel;

public record LoginResponse(String token,
                            String nome,
                            String email,
                            Papel papel) {}
