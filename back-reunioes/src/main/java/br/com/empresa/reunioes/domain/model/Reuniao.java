package br.com.empresa.reunioes.domain.model;

import br.com.empresa.reunioes.domain.enums.StatusReuniao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class Reuniao {

    private Long id;
    private String titulo;
    private String data;
    private String resumo;
    private String status;
    private List<Colaborador> participantes;
    private List<String> areas;
    private List<String> pontosChaves;
    private List<Acao> acoes;
    private Integer totalAcoes;

}
