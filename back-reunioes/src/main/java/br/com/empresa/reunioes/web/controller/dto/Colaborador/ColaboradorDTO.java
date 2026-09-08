package br.com.empresa.reunioes.web.controller.dto.Colaborador;

import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.domain.enums.Papel;

public record ColaboradorDTO(
        Long id,
        String nome,
        String email,
        Papel papel,
        Boolean monitorarReunioes,
        String dataCadastro) {
}

