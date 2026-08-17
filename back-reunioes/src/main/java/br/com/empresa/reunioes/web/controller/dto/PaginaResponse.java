package br.com.empresa.reunioes.web.controller.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Envelope unico de listagem. Evita expor o JSON do Page do Spring, que muda
 * entre versoes e carrega campo interno que o cliente nao usa.
 */
public record PaginaResponse<T>(List<T> content,
                                int page,
                                int size,
                                long totalElements,
                                int totalPages) {

    public static <T> PaginaResponse<T> de(Page<T> pagina) {
        return new PaginaResponse<>(
                pagina.getContent(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages());
    }
}
