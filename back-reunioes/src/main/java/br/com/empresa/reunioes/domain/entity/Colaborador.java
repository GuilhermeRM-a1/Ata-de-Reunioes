package br.com.empresa.reunioes.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
public class Colaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String senha;
    private Boolean monitorarReunioes;
    private String dataCadastro;

    @OneToMany
    private List<Acao> acoes;

    @ManyToMany(mappedBy = "participantes")
    private List<Reuniao> reunioes;

}
