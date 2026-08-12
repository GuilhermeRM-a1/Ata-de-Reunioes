package br.com.empresa.reunioes.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Reuniao {

    private Long id;
    private String titulo;
    private String data;
    private String resumo;
    private StatusReuniao status;
    private List<Colaborador> participantes;
    private List<String> areas;
    private Integer totalAcoes;

}
