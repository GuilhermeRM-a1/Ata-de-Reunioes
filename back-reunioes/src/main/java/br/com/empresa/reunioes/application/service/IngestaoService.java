package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoResumoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Porta de entrada do que vem da IA.
 *
 * Recebe apenas dois textos — resumo executivo e transcricao pura — e grava
 * na reuniao. Nada mais entra por aqui: participantes, acoes e responsaveis
 * sao o que o administrador registrou, e e isso que o relatorio usa como
 * verdade.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IngestaoService {

    private final ReuniaoRepository reuniaoRepository;
    private final ReuniaoService reuniaoService;

    @Transactional
    public Reuniao receberAnalise(Long reuniaoId, ReuniaoResumoRequest request) {
        log.info("Recebendo análise da IA para a reunião {}.", reuniaoId);

        Reuniao reuniao = reuniaoService.buscarPorId(reuniaoId);

        boolean reingestao = reuniao.getResumo() != null && !reuniao.getResumo().isBlank();

        if (reingestao) {
            log.warn("Reunião {} já tinha resumo; o texto anterior será substituído.", reuniaoId);
        }

        reuniao.setResumo(request.resumo().trim());

        // Campo opcional: reenvio so do resumo nao pode apagar a transcricao ja gravada.
        if (request.transcricao() != null && !request.transcricao().isBlank()) {
            reuniao.setTranscricao(request.transcricao().trim());
        }

        Reuniao salva = reuniaoRepository.save(reuniao);

        log.info("Análise gravada na reunião {} ({} caracteres de resumo).",
                reuniaoId, salva.getResumo().length());

        return salva;
    }
}
