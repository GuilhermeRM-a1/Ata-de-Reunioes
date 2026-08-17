package br.com.empresa.reunioes.web.controller.dto.Acao;

import br.com.empresa.reunioes.domain.model.Acao;
import br.com.empresa.reunioes.domain.model.Colaborador;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorDTO;

import java.util.List;

public record AcaoDTO(
        String id,
        String titulo,
        String descricao,
        List<String> responsavel,
        String prazo) {

    /** Converte direto da entidade, sem a camada passar por DTO intermediario. */
    public static AcaoDTO de(Acao acao) {

        List<String> responsavelNomes = acao.getResponsavel() == null
                ? List.of()
                : acao.getResponsavel().stream().map(Colaborador::getNome).toList();

        return new AcaoDTO(
                acao.getId() == null ? null : String.valueOf(acao.getId()),
                acao.getTitulo(),
                acao.getDescricao(),
                responsavelNomes,
                acao.getPrazo()
        );
    }

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
