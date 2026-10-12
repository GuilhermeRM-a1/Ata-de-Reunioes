package br.com.empresa.reunioes.web.reuniao.dto;

import br.com.empresa.reunioes.domain.enums.StatusReuniao;
import br.com.empresa.reunioes.domain.enums.StatusTranscricao;
import br.com.empresa.reunioes.web.acao.dto.AcaoDTO;
import br.com.empresa.reunioes.web.colaborador.dto.ColaboradorDTO;

import java.util.List;

public record RelatorioReuniaoResponse(String titulo,
                                       String data,
                                       String resumo,
                                       StatusTranscricao statusTranscricao,
                                       StatusReuniao statusReuniao,
                                       List<ColaboradorDTO> participantes,
                                       List<String> areas,
                                       List<String> pontosChaves,
                                       List<AcaoDTO> acoes,
                                       Integer totalAcoes) {
}
