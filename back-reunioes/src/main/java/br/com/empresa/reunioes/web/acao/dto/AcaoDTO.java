package br.com.empresa.reunioes.web.acao.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record AcaoDTO(
        Long id,
        String titulo,
        String tipo,
        String descricao,
        List<String> responsavel,
        String prazo,
        Boolean concluida,
        @NotBlank
        Long reuniaoId) {
}
