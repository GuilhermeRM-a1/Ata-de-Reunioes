package br.com.empresa.reunioes.web.controller.dto.Acao;

import br.com.empresa.reunioes.domain.entity.Acao;
import br.com.empresa.reunioes.domain.entity.Colaborador;
import br.com.empresa.reunioes.web.controller.dto.Colaborador.ColaboradorDTO;

import java.util.List;

public record AcaoDTO(
        String id,
        String titulo,
        String descricao,
        List<String> responsavel,
        String prazo,
        Boolean concluida,
        Long reuniaoId) {
}
