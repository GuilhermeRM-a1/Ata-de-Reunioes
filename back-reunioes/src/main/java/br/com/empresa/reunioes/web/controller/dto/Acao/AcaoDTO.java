package br.com.empresa.reunioes.web.controller.dto.Acao;

import br.com.empresa.reunioes.domain.model.Colaborador;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorDTO;

import java.util.List;

public record AcaoDTO(
        String id,
        String titulo,
        String descricao,
        List<String> responsavel,
        String prazo) {
    public static AcaoDTO de(String id, String titulo, String descricao,
                             List<ColaboradorDTO> responsavel, String prazo) {

        List<String> responsavelNomes = responsavel.stream()
                .map(ColaboradorDTO::nome)
                .toList();

        return new AcaoDTO(
                id,
                titulo,
                descricao,
                responsavelNomes,
                prazo
        );
    }
}
