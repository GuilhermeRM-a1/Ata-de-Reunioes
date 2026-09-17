package br.com.empresa.reunioes.web.controller.dto.Reuniao;

import jakarta.validation.constraints.NotBlank;

/**
 * Entrada da ingestao: o que a IA devolve depois de processar o audio.
 *
 * Sao os unicos dois textos que chegam de fora. Todo o resto do relatorio a
 * propria API monta com o que o administrador registrou na reuniao.
 */
public record ReuniaoResumoRequest(

        @NotBlank(message = "O resumo executivo é obrigatório")
        String resumo,

        /** Texto longo. Opcional: nem toda ingestao traz a transcricao. */
        String transcricao) {
}
