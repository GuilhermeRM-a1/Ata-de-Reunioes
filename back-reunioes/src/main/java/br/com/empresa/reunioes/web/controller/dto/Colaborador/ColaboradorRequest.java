package br.com.empresa.reunioes.web.controller.dto.Colaborador;

import br.com.empresa.reunioes.domain.enums.Papel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Corpo do POST e do PUT, os dois marcados com @Valid no controller.
 * O PATCH tem record proprio, o ColaboradorPatchRequest, e tambem valida —
 * o que muda la e que nenhum campo e obrigatorio.
 *
 * Atencao ao tipo: @NotBlank so vale para texto. Em enum use @NotNull,
 * senao o validador nao encontra implementacao e a requisicao morre com 500
 * antes mesmo de o corpo ser lido.
 */
public record ColaboradorRequest(@NotBlank(message = "O nome é obrigatório")
                                 String nome,

                                 @NotBlank(message = "O e-mail é obrigatório")
                                 @Email(message = "E-mail inválido")
                                 String email,

                                 @NotBlank(message = "A senha é obrigatória")
                                 String senha,

                                 @NotNull(message = "O papel é obrigatório")
                                 Papel papel,

                                 Boolean monitorarReunioes,
                                 String dataCadastro) {}
