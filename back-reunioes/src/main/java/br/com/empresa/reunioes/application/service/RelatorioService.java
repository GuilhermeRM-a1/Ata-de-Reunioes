package br.com.empresa.reunioes.application.service;

import br.com.empresa.reunioes.application.mapper.AcaoMapper;
import br.com.empresa.reunioes.application.mapper.ColaboradorMapper;
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
    private final ColaboradorMapper colaboradorMapper;
    private final AcaoMapper acaoMapper;

    @Transactional(readOnly = true)
    public RelatorioReuniaoResponse gerar(Long reuniaoId) {
        log.info("Gerando relatório consolidado da reunião {}.", reuniaoId);

        Reuniao reuniao = reuniaoService.buscarPorId(reuniaoId);

        exigirResumo(reuniao);

        List<ColaboradorDTO> participantes = reuniao.getParticipantes() == null
                ? List.of()
                : reuniao.getParticipantes().stream().map(colaboradorMapper::toDTO).toList();

        List<AcaoDTO> acoes = reuniao.getAcoes() == null
                ? List.of()
                : reuniao.getAcoes().stream().map(acaoMapper::toDTO).toList();

        log.debug("Relatório da reunião {}: {} participantes, {} ações.",
                reuniaoId, participantes.size(), acoes.size());

        return RelatorioReuniaoResponse.de(
                reuniao.getTitulo(),
                reuniao.getData(),
                reuniao.getResumo(),
                reuniao.getStatusTranscricao(),
                reuniao.getStatusReuniao(),
                participantes,
                reuniao.getAreas() == null ? List.of() : reuniao.getAreas(),
                reuniao.getPontosChaves() == null ? List.of() : reuniao.getPontosChaves(),
                acoes,
                // Contado na hora, a partir das acoes reais: o campo gravado pode
                // ter desencontrado se alguem removeu acao por outro caminho.
                acoes.size());
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
