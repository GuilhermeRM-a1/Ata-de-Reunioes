package br.com.empresa.reunioes.web.controller.dto.Acao;

import jakarta.validation.constraints.Pattern;

import java.util.List;

/**
 * Corpo do PATCH de acao. Nenhum campo e obrigatorio: nulo significa
 * "nao mexer". Sem este record o PATCH reutilizava o AcaoRequest, que cobra
 * titulo com @NotBlank — concluir uma acao mandando so {"concluida": true}
 * devolvia 400.
 *
 * @Pattern aceita nulo e barra titulo vazio ou so com espaco, que antes
 * passaria direto para o banco.
 */
public record AcaoPatchRequest(@Pattern(regexp = ".*\\S.*", message = "O título não pode ser vazio")
                               String titulo,

                               String descricao,
                               String tipo,
                               String prazo,
                               Boolean concluida,
                               List<Long> responsavel) {

    /** Adapta para o record completo, que e o que o service ja consome. */
    public AcaoRequest paraRequest() {
        return new AcaoRequest(titulo, descricao, tipo, prazo, concluida, responsavel);
    }
}
