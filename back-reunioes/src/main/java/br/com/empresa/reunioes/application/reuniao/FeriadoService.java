package br.com.empresa.reunioes.application.reuniao;

import br.com.empresa.reunioes.domain.exception.RecursoNaoEncontradoException;
import br.com.empresa.reunioes.infra.client.FeriadoClient;
import br.com.empresa.reunioes.infra.client.dto.FeriadoClientResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Cruza uma data com o calendario nacional de feriados, que vem de uma API
 * externa consumida por Feign.
 *
 * Devolve so o feriado encontrado (ou vazio, se for dia comum). Montar a
 * resposta da API e trabalho do controller.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeriadoService {

    private final FeriadoClient feriadoClient;

    /**
     * Aceita "2026-06-02" ou "2026-06-02T09:00:00". Lanca
     * RecursoNaoEncontradoException se a data nao tiver formato reconhecivel.
     */
    public Optional<FeriadoClientResponse> buscarPorData(String data) {
        log.info("Verificando se a data {} cai em feriado.", data);

        String dia = extrairDia(data);

        if (dia == null) {
            log.warn("Data em formato inesperado: {}", data);

            throw new RecursoNaoEncontradoException(
                    "A data não está em formato reconhecível (aaaa-MM-dd): " + data);
        }

        int ano = Integer.parseInt(dia.substring(0, 4));

        log.debug("Consultando a API externa de feriados para o ano {}.", ano);
        List<FeriadoClientResponse> feriados = feriadoClient.listarPorAno(ano);
        log.debug("API externa devolveu {} feriados.", feriados.size());

        return feriados.stream()
                .filter(f -> dia.equals(f.date()))
                .findFirst();
    }

    /**
     * A data da reuniao e String livre, sem validacao de formato no banco.
     * Devolve null no que nao reconhecer, nunca estoura.
     */
    private String extrairDia(String data) {

        if (data == null || data.length() < 10) {
            return null;
        }

        String dia = data.substring(0, 10);

        return dia.matches("\\d{4}-\\d{2}-\\d{2}") ? dia : null;
    }
}