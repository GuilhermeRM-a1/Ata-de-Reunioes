package br.com.empresa.reunioes.web.controller.dto.Reuniao;

import br.com.empresa.reunioes.web.controller.dto.Acao.AcaoDTO;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorDTO;

import java.util.List;

public record RelatorioReuniaoResponse(String titulo,
                                       String data,
                                       String resumo,
                                       String status,
                                       List<ColaboradorDTO> participantes,
                                       List<String> areas,
                                       List<String> pontosChaves,
                                       List<AcaoDTO> acoes,
                                       Integer totalAcoes) {

    public static RelatorioReuniaoResponse de(String titulo,
                                              String data,
                                              String resumo,
                                              String status,
                                              List<ColaboradorDTO> participantes,
                                              List<String> areas,
                                              List<String> pontosChaves,
                                              List<AcaoDTO> acoes,
                                              Integer totalAcoes){

        return new RelatorioReuniaoResponse(
                titulo,
                data,
                resumo,
                status,
                participantes,
                areas,
                pontosChaves,
                acoes,
                totalAcoes);

    }
}
