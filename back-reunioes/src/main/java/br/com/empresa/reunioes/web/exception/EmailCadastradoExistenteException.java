package br.com.empresa.reunioes.web.exception;

public class EmailCadastradoExistenteException extends RuntimeException {
    public EmailCadastradoExistenteException(String message) {
        super(message);
    }
}
