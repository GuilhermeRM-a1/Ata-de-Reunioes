package br.com.empresa.reunioes.web.controller.dto.Acao;

import java.util.List;

public record AcaoRequest(String titulo,
                          String descricao,
                          String tipo,
                          String prazo,
                          List<Long> responsavel,
                          Long reuniao) {
}
