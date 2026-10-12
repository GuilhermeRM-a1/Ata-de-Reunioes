package br.com.empresa.reunioes.web.colaborador.mapper;

import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.web.colaborador.dto.ColaboradorDTO;

public class ColaboradorWebMapper {

    public static ColaboradorDTO colaboradorToDTO(Colaborador colaborador) {

        return new ColaboradorDTO(
                colaborador.getId(),
                colaborador.getNome(),
                colaborador.getEmail(),
                colaborador.getPapel(),
                colaborador.getMonitorarReunioes(),
                colaborador.getDataCadastro()
        );
    }

}
