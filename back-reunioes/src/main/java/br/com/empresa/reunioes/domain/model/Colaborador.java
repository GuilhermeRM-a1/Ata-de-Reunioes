package br.com.empresa.reunioes.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class Colaborador {

    private Long id;
    private String nome;
    private String senha;
    private Boolean monitorarReunioes;
    private String dataCadastro;
    private List<Acao> acoes;
    private List<Reuniao> reunioes;

}
