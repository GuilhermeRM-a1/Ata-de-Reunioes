package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.web.controller.dto.Acao.AcaoDTO;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorDTO;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.RelatorioReuniaoResponse;
import br.com.empresa.reunioes.web.exception.RelatorioIndisponivelException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.empresa.reunioes.application.mapper.AcaoMapper.acaoToDTO;
import static br.com.empresa.reunioes.application.mapper.ColaboradorMapper.colaboradorToDTO;
import static br.com.empresa.reunioes.application.mapper.RelatorioMapper.montarRelatorio;

/**
 * Monta o relatorio consolidado da reuniao.
 *
 * A camada operacional e a fonte da verdade: participantes, acoes,
 * responsaveis, areas e pontos-chave saem do que o administrador registrou.
 * Da IA entra so o resumo executivo, como texto de apoio.
 *
 * E por isso que o relatorio corrige o problema descrito na proposta — a IA
 * erra quem estava na reuniao e quem ficou com cada acao; aqui esses dados
 * nao vem dela.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final ReuniaoService reuniaoService;

    @Transactional(readOnly = true)
    public Reuniao validarReuniaoParaRelatorio(Long reuniaoId) {
        log.info("Gerando relatório consolidado da reunião {}.", reuniaoId);

        Reuniao reuniao = reuniaoService.buscarPorId(reuniaoId);

        exigirResumo(reuniao);

        return reuniao;
    }

    /**
     * Sem o resumo da IA o relatorio sairia pela metade e pareceria pronto.
     * Melhor recusar e dizer o que falta.
     */
    private void exigirResumo(Reuniao reuniao) {

        if (reuniao.getResumo() == null || reuniao.getResumo().isBlank()) {

            log.warn("Relatório da reunião {} recusado: resumo da IA ainda não chegou.",
                    reuniao.getId());

            throw new RelatorioIndisponivelException(
                    "A reunião " + reuniao.getId() + " ainda não recebeu o resumo da IA. "
                            + "Envie a análise em PATCH /api/reunioes/" + reuniao.getId()
                            + "/ingestao antes de gerar o relatório.");
        }
    }
}
