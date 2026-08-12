package br.com.empresa.reunioes.application.mapper;
import br.com.empresa.reunioes.domain.model.Reuniao;
import br.com.empresa.reunioes.web.controller.dto.ReuniaoRequest;
import br.com.empresa.reunioes.web.controller.dto.ReuniaoResponse;
import br.com.empresa.reunioes.web.controller.dto.ReuniaoResumoResponse;
import org.springframework.stereotype.Component;

@Component
public class ReuniaoMapper {

    // 1. Recebe DTO de requisição e retorna a Entidade
    public Reuniao toEntity(ReuniaoRequest request) {
        if (request == null) return null;

        Reuniao entity = new Reuniao();
        entity.setTitulo(request.titulo());
        entity.setData(request.data());
        entity.setResumo(request.resumo());
        entity.setStatus(request.status());
        entity.setParticipantes(request.participantes());
        entity.setAreas(request.areas());
        entity.setTotalAcoes(request.totalAcoes());
        return entity;
    }

    // 2. Recebe a Entidade e retorna o DTO de resposta completo
    public ReuniaoResponse toResponse(Reuniao entity) {
        if (entity == null) return null;

        return ReuniaoResponse.de(entity);
    }

    // 3. Recebe a Entidade e retorna o Resumo (REGRA: Não copiar transcrição!)
    public ReuniaoResumoResponse toResumo(Reuniao entity) {
        if (entity == null) return null;

        // IMPORTANTE: Não defina/copie o campo transcrição aqui

        return null;
    }
}
