package br.com.empresa.reunioes.application.colaborador.mapper;

import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.web.colaborador.dto.request.ColaboradorRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class ColaboradorMapper {

    private ColaboradorMapper() {

    }

    public static Colaborador colaboradorToEntity(ColaboradorRequest request) {
        log.info("Mapper convertendo...");

        Colaborador colaborador = new Colaborador();

        colaborador.setNome(request.nome());
        colaborador.setEmail(request.email());
        colaborador.setSenha(request.senha());
        colaborador.setPapel(request.papel());
        colaborador.setMonitorarReunioes(request.monitorarReunioes());
        colaborador.setDataCadastro(request.dataCadastro());

        log.debug("Requisição convertida para Entidade");

        return colaborador;
    }
}