package br.com.empresa.reunioes.web.login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(@NotBlank(message = "O nome é obrigatório")
                              String nome,

                              @NotBlank(message = "O e-mail é obrigatório")
                              @Email(message = "E-mail inválido")
                              String email,

                              @NotBlank(message = "A senha é obrigatória")
                              @Size(min = 8, max = 20, message = "A senha deve ter entre 8 e 20 caracteres")
                              String senha,

                              Boolean monitorarReuniao) {}
