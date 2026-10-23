package br.com.empresa.reunioes.web.acao.mapper;

import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.web.acao.dto.AcaoDTO;

import java.util.List;

public final class AcaoWebMapper {

    private AcaoWebMapper() {
    }

    public static AcaoDTO acaoToDTO(Acao acao) {
        List<String> responsavelNomes = acao.getResponsavel() == null
                ? List.of()
                : acao.getResponsavel().stream()
                .map(Colaborador::getNome)
                .toList();

        return new AcaoDTO(
                acao.getId(),
                acao.getTitulo(),
                acao.getTipo(),
                acao.getDescricao(),
                responsavelNomes,
                acao.getPrazo(),
                acao.getConcluida(),
                acao.getReuniao().getId()
        );
    }
}
