package br.com.empresa.reunioes.web.controller.dto;

import br.com.empresa.reunioes.domain.model.Colaborador;
import br.com.empresa.reunioes.domain.model.Reuniao;

public record ColaboradorResponse(
        String email,
        String nome,
        Boolean monitorarReunioes,
        String dataCadastro) {
    public static ColaboradorResponse de(Colaborador colaborador) {
        return new ColaboradorResponse(
                colaborador.getEmail(),
                colaborador.getNome(),
                colaborador.getMonitorarReunioes(),
                colaborador.getDataCadastro()
        );

    }
}
