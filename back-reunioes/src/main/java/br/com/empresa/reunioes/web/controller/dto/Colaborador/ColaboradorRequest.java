package br.com.empresa.reunioes.web.controller.dto.Colaborador;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * As restricoes so sao cobradas onde o controller marca @Valid — POST e PUT.
 * O PATCH usa o mesmo record sem validar, porque ali campo nulo significa
 * "nao mexer".
 */
public record ColaboradorRequest(@NotBlank(message = "O nome é obrigatório")
                                 String nome,

                                 @NotBlank(message = "O e-mail é obrigatório")
                                 @Email(message = "E-mail inválido")
                                 String email,

                                 @NotBlank(message = "A senha é obrigatória")
                                 String senha,

                                 Boolean monitorarReunioes,
                                 String dataCadastro) {}
