package br.com.empresa.reunioes.web.login.dto;

import br.com.empresa.reunioes.domain.enums.Papel;

/** Formato que o AuthService do front espera: token, nome, email e papel. */
public record LoginResponse(String token,
                            String nome,
                            String email,
                            Papel papel) {}