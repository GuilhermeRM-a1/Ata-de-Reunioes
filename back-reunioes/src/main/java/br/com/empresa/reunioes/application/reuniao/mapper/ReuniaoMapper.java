package br.com.empresa.reunioes.application.reuniao.mapper;

import br.com.empresa.reunioes.domain.entity.Reuniao;
import br.com.empresa.reunioes.web.reuniao.dto.ReuniaoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * O mapper traz uma separação de responsabilidade
 * Não é amplamente usado, serve apenas para trazer um objeto novo ao salvar
 * Mas seu conceito é interessante
 */

@Slf4j
@Component
public class ReuniaoMapper {

    public static Reuniao reuniaoToEntity(ReuniaoRequest request) {
        log.info("Mapper Convertendo...");

        Reuniao reuniao = new Reuniao();

        reuniao.setTitulo(request.titulo());
        reuniao.setData(request.data());
        reuniao.setStatusTranscricao(request.statusTranscricao());
        reuniao.setStatusReuniao(request.statusReuniao());
        reuniao.setAreas(request.areas());
        reuniao.setPontosChaves(request.pontosChaves());
        reuniao.setResumo(request.resumo());

        log.debug("Requisição convertida para entidade");

        return reuniao;
    }
}
