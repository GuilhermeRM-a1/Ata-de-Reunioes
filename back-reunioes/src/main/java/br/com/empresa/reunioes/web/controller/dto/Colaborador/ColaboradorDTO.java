package br.com.empresa.reunioes.web.controller.dto.Colaborador;

import br.com.empresa.reunioes.domain.entity.Colaborador;

public record ColaboradorDTO(
        Long id,
        String nome,
        Boolean monitorarReunioes,
        String dataCadastro) {
}

