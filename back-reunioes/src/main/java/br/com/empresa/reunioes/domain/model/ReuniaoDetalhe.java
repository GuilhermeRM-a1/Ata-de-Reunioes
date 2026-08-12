package br.com.empresa.reunioes.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ReuniaoDetalhe extends Reuniao{

    private String transcricaoPura;
    private String pontoChave;
    private List<Acao> acoes;

}