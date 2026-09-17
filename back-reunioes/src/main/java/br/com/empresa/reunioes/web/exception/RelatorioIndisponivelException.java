package br.com.empresa.reunioes.web.exception;

/**
 * A reuniao existe, mas ainda nao reune o que o relatorio precisa.
 *
 * Nao e 404 — o recurso esta la. E conflito de estado: a operacao so faz
 * sentido depois que a ingestao acontecer. O handler traduz para 409.
 */
public class RelatorioIndisponivelException extends RuntimeException {

    public RelatorioIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
