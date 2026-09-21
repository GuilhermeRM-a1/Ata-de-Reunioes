package br.com.empresa.reunioes.web.controller.dto.Reuniao;

import br.com.empresa.reunioes.domain.enums.StatusReuniao;
import br.com.empresa.reunioes.domain.enums.StatusTranscricao;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Corpo do PATCH de reuniao. Nenhum campo e obrigatorio: nulo significa
 * "nao mexer". Sem este record o PATCH reutilizava o ReuniaoRequest, que
 * cobra titulo, data e os dois status — mandar um campo so devolvia 400 e
 * a atualizacao parcial nao funcionava.
 *
 * O que se valida aqui e o conteudo do campo QUE FOI enviado: @Size aceita
 * nulo e barra titulo curto demais.
 */
public record ReuniaoPatchRequest(@Size(min = 5, message = "O título deve ter ao menos 5 caracteres")
                                  String titulo,

                                  String data,

                                  StatusTranscricao statusTranscricao,

                                  StatusReuniao statusReuniao,

                                  List<String> areas,
                                  List<String> pontosChaves,
                                  List<Long> participantes,
                                  List<Long> acoes,
                                  String resumo) {

    /** Adapta para o record completo, que e o que o service ja consome. */
    public ReuniaoRequest paraRequest() {
        return new ReuniaoRequest(titulo, data, statusTranscricao, statusReuniao,
                areas, pontosChaves, participantes, acoes, resumo);
    }
}
