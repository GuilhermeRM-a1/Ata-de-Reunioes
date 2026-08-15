package br.com.empresa.reunioes.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class Acao {

    private Long id;
    private String titulo;
    private String descricao;
    private String tipo;
    private String prazo;
    private List<Colaborador> responsavel;
    private Reuniao reuniao;

}
