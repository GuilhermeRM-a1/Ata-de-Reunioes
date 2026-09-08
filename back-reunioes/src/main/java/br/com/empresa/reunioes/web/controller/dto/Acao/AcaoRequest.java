package br.com.empresa.reunioes.web.controller.dto.Acao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AcaoRequest(@NotBlank
                          String titulo,
                          String descricao,
                          String tipo,
                          String prazo,
                          List<Long> responsavel) {
}
