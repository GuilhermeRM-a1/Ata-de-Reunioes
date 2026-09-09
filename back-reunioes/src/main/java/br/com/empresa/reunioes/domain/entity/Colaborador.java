package br.com.empresa.reunioes.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "colaborador")
public class Colaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    private String senha;

    @Column(name = "monitoramento_reunioes")
    private Boolean monitorarReunioes;

    @Column(name = "data_cadastro")
    private String dataCadastro;

    @ManyToMany(mappedBy = "responsavel")
    private List<Acao> acoes;


    @ManyToMany(mappedBy = "participantes")
    private List<Reuniao> reunioes;

}
