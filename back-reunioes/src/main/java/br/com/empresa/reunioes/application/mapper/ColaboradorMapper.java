package br.com.empresa.reunioes.application.mapper;

import br.com.empresa.reunioes.domain.model.Colaborador;
import br.com.empresa.reunioes.domain.model.Reuniao;
import br.com.empresa.reunioes.web.controller.dto.*;
import org.springframework.stereotype.Component;

@Component
public class ColaboradorMapper {

    public Colaborador toEntity(ColaboradorRequest request) {
        if (request == null) return null;

        Colaborador entity = new Colaborador();
        entity.setEmail(request.email());
        entity.setNome(request.nome());
        entity.setMonitorarReunioes(request.monitorarReunioes());
        entity.setDataCadastro(request.dataCadastro());
        return entity;
    }

    // 2. Recebe a Entidade e retorna o DTO de resposta completo
    public ColaboradorResponse toResponse(Colaborador entity) {
        if (entity == null) return null;

        return ColaboradorResponse.de(entity);
    }

    // 3. Recebe a Entidade e retorna o Resumo (REGRA: Não copiar transcrição!)
    public ReuniaoResumoResponse toResumo(Reuniao entity) {
        if (entity == null) return null;

        // IMPORTANTE: Não defina/copie o campo transcrição aqui

        return null;
    }

}
