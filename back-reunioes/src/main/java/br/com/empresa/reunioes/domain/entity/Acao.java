package br.com.empresa.reunioes.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
public class Acao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;
    private String tipo;
    private String prazo;

    @ManyToMany
    private List<Colaborador> responsavel;

    @ManyToOne
    @JoinColumn(name = "reuniao_id")
    private Reuniao reuniao;

}
