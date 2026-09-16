package br.com.empresa.reunioes.web.controller.dto.Reuniao;

/**
 * Resposta do cruzamento entre a data da reuniao e o calendario de feriados
 * nacionais vindo da API externa.
 */
public record ReuniaoFeriadoDTO(Long reuniaoId,
                                String titulo,
                                String data,
                                boolean feriado,
                                String nomeFeriado,
                                String diaSemana) {

    public static ReuniaoFeriadoDTO emFeriado(Long id, String titulo, String data,
                                              String nomeFeriado, String diaSemana) {
        return new ReuniaoFeriadoDTO(id, titulo, data, true, nomeFeriado, diaSemana);
    }

    public static ReuniaoFeriadoDTO diaComum(Long id, String titulo, String data) {
        return new ReuniaoFeriadoDTO(id, titulo, data, false, null, null);
    }
}
