package br.com.empresa.reunioes.application.acao.mapper;

import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.web.acao.dto.AcaoRequest;
import org.springframework.stereotype.Component;

@Component
public class AcaoMapper {

    public static Acao acaoToEntity(AcaoRequest request) {
        Acao acao = new Acao();

        acao.setTitulo(request.titulo());
        acao.setDescricao(request.descricao());
        acao.setTipo(request.tipo());
        acao.setPrazo(request.prazo());
        acao.setConcluida(request.concluida() != null ? request.concluida() : false);

        return acao;
    }

    public static void acaoUpdateEntity(Acao acao, AcaoRequest request) {
        acao.setTitulo(request.titulo());
        acao.setDescricao(request.descricao());
        acao.setTipo(request.tipo());
        acao.setPrazo(request.prazo());
        acao.setConcluida(request.concluida() != null ? request.concluida() : false);
    }

    public static void acaoUpdateParcialEntity(Acao acao, AcaoRequest request) {
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

        if (request.concluida() != null) {
            acao.setConcluida(request.concluida());
        }
    }
}