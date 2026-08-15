package br.com.empresa.reunioes.web.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Traduz excecao em resposta padronizada no formato ProblemDetail (RFC 7807).
 * Nenhum handler devolve rastreamento de pilha: o stack trace vai para o log
 * do servidor, e o cliente recebe so a mensagem.
 */
@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail tratarNaoEncontrado(RecursoNaoEncontradoException e) {

        return montar(HttpStatus.NOT_FOUND, "Recurso não encontrado", e.getMessage(),
                "recurso-nao-encontrado");
    }

    /** Corpo reprovado na validacao — devolve a lista de campos rejeitados. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail tratarValidacao(MethodArgumentNotValidException e) {

        List<Map<String, String>> campos = e.getBindingResult().getFieldErrors().stream()
                .map(this::descreverCampo)
                .toList();

        ProblemDetail problema = montar(HttpStatus.BAD_REQUEST, "Campos inválidos",
                "Um ou mais campos foram rejeitados na validação.", "campos-invalidos");

        problema.setProperty("campos", campos);

        return problema;
    }

    /** JSON malformado ou enum fora dos valores aceitos. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail tratarCorpoIlegivel(HttpMessageNotReadableException e) {

        return montar(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido",
                "O corpo enviado não pôde ser lido. Verifique o formato do JSON.",
                "corpo-ilegivel");
    }

    /** Id na rota com tipo errado, por exemplo /reunioes/abc. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail tratarTipoInvalido(MethodArgumentTypeMismatchException e) {

        return montar(HttpStatus.BAD_REQUEST, "Parâmetro inválido",
                "O parâmetro " + e.getName() + " recebeu um valor de tipo incompatível.",
                "parametro-invalido");
    }

    /**
     * Rede de seguranca. O detalhe da falha fica no log; a resposta nao carrega
     * stack trace, que contaria como falha de seguranca.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail tratarInesperado(Exception e) {

        log.error("Erro não tratado na API", e);

        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Ocorreu um erro inesperado ao processar a requisição.", "erro-interno");
    }

    private Map<String, String> descreverCampo(FieldError erro) {

        Map<String, String> campo = new LinkedHashMap<>();
        campo.put("campo", erro.getField());
        campo.put("mensagem", erro.getDefaultMessage());

        return campo;
    }

    private ProblemDetail montar(HttpStatus status, String titulo, String detalhe, String tipo) {

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detalhe);
        problema.setTitle(titulo);
        problema.setType(URI.create("https://api.reunioes/erros/" + tipo));
        problema.setProperty("timestamp", OffsetDateTime.now().toString());

        return problema;
    }
}
