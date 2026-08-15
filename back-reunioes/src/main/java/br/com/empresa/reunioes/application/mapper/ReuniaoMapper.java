package br.com.empresa.reunioes.application.mapper;
import br.com.empresa.reunioes.domain.model.Reuniao;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoRequest;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.ReuniaoDTO;
import br.com.empresa.reunioes.web.controller.dto.Reuniao.RelatorioReuniaoResponse;
import org.springframework.stereotype.Component;

@Component
public class ReuniaoMapper {

    // 1. Recebe DTO de requisição e retorna a Entidade
    public Reuniao toEntity(ReuniaoRequest request) {
        if (request == null) return null;
        return null;
    }

    // 2. Recebe a Entidade e retorna o DTO de resposta completo
    public ReuniaoDTO toResponse(Reuniao entity) {
        if (entity == null) return null;

        return ReuniaoDTO.de(entity);
    }

    // 3. Recebe a Entidade e retorna o Resumo (REGRA: Não copiar transcrição!)
    public RelatorioReuniaoResponse toResumo(Reuniao entity) {
        if (entity == null) return null;

        // IMPORTANTE: Não defina/copie o campo transcrição aqui

        return null;
    }
}
