package br.com.empresa.reunioes.web.controller.dto.Reuniao;


import br.com.empresa.reunioes.domain.enums.StatusTranscricao;
import br.com.empresa.reunioes.web.controller.dto.Acao.AcaoDTO;

import java.util.List;

public record ReuniaoDTO(Long id,
                         String titulo,
                         String data,
                         StatusTranscricao status,
                         List<String> participantes,
                         List<String> pontosChaves,
                         List<AcaoDTO> acoes,
                         List<String> areas,
                         String resumo,
                         Integer totalAcoes) {
}
