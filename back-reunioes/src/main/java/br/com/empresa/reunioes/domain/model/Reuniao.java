package br.com.empresa.reunioes.domain.model;

import java.util.List;

public class Reuniao {

    private Long id;
    private String tituloReuniao;
    private String dataReuniao;
    private String resumoExecutivo;
    private StatusReuniao status;
    private List<Colaborador> participantes;
    private List<String> areas;
    private Integer totalAcoes;

}
