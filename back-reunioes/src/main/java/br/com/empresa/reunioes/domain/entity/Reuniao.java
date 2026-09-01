package br.com.empresa.reunioes.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
public class Reuniao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String data;
    private String resumo;
    private String status;

    @ManyToMany
    @JoinTable(name = "reuniao_participantes",
            joinColumns = @JoinColumn(name = "reuniao_id"),
            inverseJoinColumns = @JoinColumn(name = "participantes_id"))
    private List<Colaborador> participantes;

    @ElementCollection
    @CollectionTable(name = "reuniao_areas", joinColumns = @JoinColumn(name = "reuniao_id"))
    @Column(name = "areas")
    private List<String> areas;

    @ElementCollection
    @CollectionTable(name = "reuniao_pontos_chaves", joinColumns = @JoinColumn(name = "reuniao_id"))
    @Column(name = "pontos_chaves")
    private List<String> pontosChaves;

    /** Lado inverso: a coluna reuniao_id mora na tabela acao. */
    @OneToMany(mappedBy = "reuniao")
    private List<Acao> acoes;

    private Integer totalAcoes;

}
