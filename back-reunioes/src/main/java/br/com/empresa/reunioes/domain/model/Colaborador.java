package br.com.empresa.reunioes.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Colaborador {

    private Long id;
    private String email;
    private String nome;
    private Boolean monitorarReunioes;
    private String dataCadastro;

}
