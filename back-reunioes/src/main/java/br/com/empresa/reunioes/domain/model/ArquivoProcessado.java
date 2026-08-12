package br.com.empresa.reunioes.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArquivoProcessado {

    private Long fileId;
    private String fileName;
    private String dataProcessamento;

}
