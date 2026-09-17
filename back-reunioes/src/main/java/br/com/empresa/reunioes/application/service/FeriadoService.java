package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.web.client.FeriadoClient;
import br.com.empresa.reunioes.web.client.dto.FeriadoDTO;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoFeriadoDTO;
import br.com.empresa.reunioes.web.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Cruza a data da reuniao com o calendario nacional de feriados, que vem de
 * uma API externa consumida por Feign.
 *
 * Serve para o operador perceber que marcou reuniao em feriado — que e uma das
 * inconsistencias que a proposta do projeto pede para o sistema apontar.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeriadoService {

    private final FeriadoClient feriadoClient;
    private final ReuniaoService reuniaoService;

    @Transactional(readOnly = true)
    public ReuniaoFeriadoDTO verificarReuniao(Long reuniaoId) {
        log.info("Verificando se a reunião {} caiu em feriado.", reuniaoId);

        Reuniao reuniao = reuniaoService.buscarPorId(reuniaoId);

        String dia = extrairDia(reuniao.getData());

        if (dia == null) {
            log.warn("Reunião {} está sem data ou com data em formato inesperado: {}",
                    reuniaoId, reuniao.getData());

            throw new RecursoNaoEncontradoException(
                    "A reunião " + reuniaoId + " não tem data em formato reconhecível (aaaa-MM-dd)");
        }

        int ano = Integer.parseInt(dia.substring(0, 4));

        log.debug("Consultando a API externa de feriados para o ano {}.", ano);
        List<FeriadoDTO> feriados = feriadoClient.listarPorAno(ano);
        log.debug("API externa devolveu {} feriados.", feriados.size());

        return feriados.stream()
                .filter(f -> dia.equals(f.date()))
                .findFirst()
                .map(f -> {
                    log.info("Reunião {} caiu no feriado de {}.", reuniaoId, f.name());
                    return ReuniaoFeriadoDTO.emFeriado(
                            reuniao.getId(), reuniao.getTitulo(), dia, f.name(), f.weekday());
                })
                .orElseGet(() -> {
                    log.info("Reunião {} não caiu em feriado.", reuniaoId);
                    return ReuniaoFeriadoDTO.diaComum(reuniao.getId(), reuniao.getTitulo(), dia);
                });
    }

    /**
     * A data da reuniao e String livre. Aceita tanto "2026-06-02" quanto
     * "2026-06-02T09:00:00", e devolve null no que nao reconhecer — nunca
     * estoura, porque o campo nao tem validacao de formato no banco.
     */
    private String extrairDia(String data) {

        if (data == null || data.length() < 10) {
            return null;
        }

        String dia = data.substring(0, 10);

        return dia.matches("\\d{4}-\\d{2}-\\d{2}") ? dia : null;
    }
}
