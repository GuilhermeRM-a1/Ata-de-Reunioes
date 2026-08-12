package br.com.empresa.reunioes.web.controller.dto;

import br.com.empresa.reunioes.domain.model.Colaborador;
import br.com.empresa.reunioes.domain.model.StatusReuniao;

import java.util.List;

public record ReuniaoRequest(String titulo,
                             String data,
                             String resumo,
                             StatusReuniao status,
                             List<Colaborador> participantes,
                             List<String> areas,
                             Integer totalAcoes) {}
