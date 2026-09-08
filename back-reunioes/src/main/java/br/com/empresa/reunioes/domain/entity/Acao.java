package br.com.empresa.reunioes.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "")
public class Acao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "")
    private String titulo;

    @Column(name = "")
    private String descricao;

    @Column(name = "")
    private String tipo;

    @Column(name = "")
    private String prazo;

    /** Nomes fixados na mao: o padrao do Hibernate nao bate com a migration. */
    @ManyToMany
    @JoinTable(name = "acao_responsavel",
            joinColumns = @JoinColumn(name = "acao_id"),
            inverseJoinColumns = @JoinColumn(name = "responsavel_id"))
    private List<Colaborador> responsavel;

    @ManyToOne
    @JoinColumn(name = "reuniao_id")
    private Reuniao reuniao;

}
