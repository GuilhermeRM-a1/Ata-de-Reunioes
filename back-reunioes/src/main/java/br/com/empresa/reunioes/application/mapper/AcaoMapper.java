package br.com.empresa.reunioes.application.mapper;

import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.web.controller.dto.Acao.AcaoDTO;
import br.com.empresa.reunioes.web.controller.dto.Acao.AcaoRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AcaoMapper {

    public Acao toEntity(AcaoRequest request) {
        Acao acao = new Acao();

        acao.setTitulo(request.titulo());
        acao.setDescricao(request.descricao());
        acao.setTipo(request.tipo());
        acao.setPrazo(request.prazo());

        return acao;
    }

    public void updateEntity(Acao acao, AcaoRequest request) {
        acao.setTitulo(request.titulo());
        acao.setDescricao(request.descricao());
        acao.setTipo(request.tipo());
        acao.setPrazo(request.prazo());
    }

    public void updateParsiEntity(Acao acao, AcaoRequest request) {
        if (request.titulo() != null) {
            acao.setTitulo(request.titulo());
        }

        if (request.descricao() != null) {
            acao.setDescricao(request.descricao());
        }

        if (request.tipo() != null) {
            acao.setTipo(request.tipo());
        }

        if (request.prazo() != null) {
            acao.setPrazo(request.prazo());
        }
    }

    public AcaoDTO toDTO(Acao acao) {
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
}
