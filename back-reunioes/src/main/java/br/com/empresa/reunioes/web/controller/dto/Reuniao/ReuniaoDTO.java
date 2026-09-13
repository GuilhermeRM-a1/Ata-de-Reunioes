package br.com.empresa.reunioes.web.controller.dto.Reuniao;

import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.entity.Reuniao;

import java.util.List;

public record ReuniaoDTO(Long id,
                         String titulo,
                         String data,
                         String status,
                         List<String> participantes,
                         List<String> areas,
                         Integer totalAcoes) {
}
