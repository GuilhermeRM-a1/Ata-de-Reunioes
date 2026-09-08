package br.com.empresa.reunioes.domain.entity;

import br.com.empresa.reunioes.domain.enums.Papel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "")
public class Colaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "")
    private String nome;

    @Column(name = "")
    private String senha;

    @Column(name = "")
    private Boolean monitorarReunioes;

    @Column(name = "")
    private String dataCadastro;

    @Column(name = "")
    private Boolean admin;

    @OneToMany
    private List<Acao> acoes;


    @ManyToMany(mappedBy = "participantes")
    private List<Reuniao> reunioes;

}
