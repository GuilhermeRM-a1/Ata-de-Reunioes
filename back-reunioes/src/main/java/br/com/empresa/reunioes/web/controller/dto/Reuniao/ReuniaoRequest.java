package br.com.empresa.reunioes.web.controller.dto.Reuniao;

import br.com.empresa.reunioes.domain.model.Acao;
import br.com.empresa.reunioes.domain.model.Colaborador;

import java.util.List;

public record ReuniaoRequest(String titulo,
                             String data,
                             String status,
                             List<String> areas,
                             List<String> pontosChaves,
                             List<Long> participantes,
                             List<Long> acoes,
                             Integer totalAcoes){

}
