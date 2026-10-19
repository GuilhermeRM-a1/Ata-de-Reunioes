package br.com.empresa.reunioes.domain.exception;

public class EmailCadastradoExistenteException extends RuntimeException {
    public EmailCadastradoExistenteException(String message) {
        super(message);
    }
}
