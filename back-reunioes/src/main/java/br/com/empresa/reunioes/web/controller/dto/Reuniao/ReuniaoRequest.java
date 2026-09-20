package br.com.empresa.reunioes.web.controller.dto.Reuniao;

import br.com.empresa.reunioes.domain.enums.StatusReuniao;
import br.com.empresa.reunioes.domain.enums.StatusTranscricao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

                             @NotNull(message = "O status da transcrição é obrigatório")
                             StatusTranscricao statusTranscricao,

                             @NotNull(message = "O status da reunião é obrigatório")
                             StatusReuniao statusReuniao,

                             List<String> areas,
                             List<String> pontosChaves,
                             List<Long> participantes,
                             List<Long> acoes,

                             /** Resumo executivo. Opcional: nem toda reuniao ja foi transcrita. */
                             String resumo){

}
