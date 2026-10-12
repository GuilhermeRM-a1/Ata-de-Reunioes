package br.com.empresa.reunioes.domain.exception;

/** Sinaliza identificador inexistente. O handler traduz para 404 em ProblemDetail. */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
