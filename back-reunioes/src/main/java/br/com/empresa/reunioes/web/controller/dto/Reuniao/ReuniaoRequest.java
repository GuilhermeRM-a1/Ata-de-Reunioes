package br.com.empresa.reunioes.web.controller.dto.Reuniao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * As restricoes so sao cobradas onde o controller marca @Valid — POST e PUT.
 * O PATCH usa o mesmo record sem validar, porque ali campo nulo significa
 * "nao mexer".
 */
public record ReuniaoRequest(@NotBlank(message = "O título é obrigatório")
                             @Size(min = 5, message = "O título deve ter ao menos 5 caracteres")
                             String titulo,

                             @NotBlank(message = "A data é obrigatória")
                             String data,

                             @NotBlank(message = "O status é obrigatório")
                             String status,

                             List<String> areas,
                             List<String> pontosChaves,
                             List<Long> participantes,
                             List<Long> acoes,

                             @PositiveOrZero(message = "O total de ações não pode ser negativo")
                             Integer totalAcoes){

}
