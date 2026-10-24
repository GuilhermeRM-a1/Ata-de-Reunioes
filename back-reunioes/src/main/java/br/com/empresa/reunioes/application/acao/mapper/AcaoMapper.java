package br.com.empresa.reunioes.application.acao.mapper;

import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.web.acao.dto.request.AcaoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class AcaoMapper {

    private AcaoMapper() {

    }

    public static Acao acaoToEntity(AcaoRequest request) {
        log.info("Mapper convertendo...");

        Acao acao = new Acao();

        acao.setTitulo(request.titulo());
        acao.setDescricao(request.descricao());
        acao.setTipo(request.tipo());
        acao.setPrazo(request.prazo());
        acao.setConcluida(Boolean.TRUE.equals(request.concluida()));

        log.debug("Requisição convertida para Entidade com sucesso");

        return acao;
    }
}