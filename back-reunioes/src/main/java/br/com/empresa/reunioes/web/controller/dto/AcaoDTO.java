package br.com.empresa.reunioes.web.controller.dto;

import br.com.empresa.reunioes.domain.model.Acao;
import br.com.empresa.reunioes.domain.model.Colaborador;

public record AcaoDTO(String descricao, Colaborador responsavel, String prazo) {}
