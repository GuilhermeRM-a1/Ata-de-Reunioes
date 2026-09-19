package br.com.empresa.reunioes.web.controller.dto.Acao;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record AcaoDTO(
        String id,
        String titulo,
        String tipo,
        String descricao,
        List<String> responsavel,
        String prazo,
        Boolean concluida,
        @NotBlank
        Long reuniaoId) {
}
