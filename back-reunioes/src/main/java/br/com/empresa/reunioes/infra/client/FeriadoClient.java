package br.com.empresa.reunioes.infra.client;

import br.com.empresa.reunioes.infra.client.dto.FeriadoClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Cliente declarativo da BrasilAPI. A URL vem do application.yml, para nao
 * ficar presa no codigo e poder ser trocada por ambiente.
 *
 * API publica, sem chave e sem cadastro:
 * https://brasilapi.com.br/api/feriados/v1/{ano}
 */
@FeignClient(name = "brasilApiFeriados", url = "${api.feriados.url}")
public interface FeriadoClient {

    /** Todos os feriados nacionais de um ano. */
    @GetMapping("/{ano}")
    List<FeriadoClientResponse> listarPorAno(@PathVariable("ano") int ano);
}
