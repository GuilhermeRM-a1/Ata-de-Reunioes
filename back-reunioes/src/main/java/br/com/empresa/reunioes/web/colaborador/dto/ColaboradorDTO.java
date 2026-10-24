package br.com.empresa.reunioes.web.colaborador.dto;

import br.com.empresa.reunioes.domain.enums.Papel;

public record ColaboradorDTO(
        Long id,
        String nome,
        String email,
        Papel papel,
        Boolean monitorarReunioes,
        String dataCadastro) {
}