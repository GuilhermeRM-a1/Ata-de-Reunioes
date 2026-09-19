package br.com.empresa.reunioes.web.exception;

/** Sinaliza identificador inexistente. O handler traduz para 404 em ProblemDetail. */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
