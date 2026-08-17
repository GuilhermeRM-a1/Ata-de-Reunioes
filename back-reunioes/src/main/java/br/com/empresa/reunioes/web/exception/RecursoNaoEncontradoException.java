package br.com.empresa.reunioes.web.exception;

/** Sinaliza id inexistente. O handler traduz para 404 em ProblemDetail. */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public static RecursoNaoEncontradoException de(String recurso, Object id) {
        return new RecursoNaoEncontradoException(recurso + " não encontrada(o) para o id " + id);
    }
}
