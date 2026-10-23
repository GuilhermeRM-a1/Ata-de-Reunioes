package br.com.empresa.reunioes.web.reuniao.dto;


import br.com.empresa.reunioes.domain.enums.StatusReuniao;
import br.com.empresa.reunioes.domain.enums.StatusTranscricao;
import br.com.empresa.reunioes.web.acao.dto.AcaoDTO;

import java.util.List;

public record ReuniaoDTO(Long id,
                         String titulo,
                         String data,
                         StatusTranscricao statusTranscricao,
                         StatusReuniao statusReuniao,
                         List<String> participantes,
                         List<String> pontosChaves,
                         List<AcaoDTO> acoes,
                         List<String> areas,
                         String resumo,
                         Integer totalAcoes) {
}
