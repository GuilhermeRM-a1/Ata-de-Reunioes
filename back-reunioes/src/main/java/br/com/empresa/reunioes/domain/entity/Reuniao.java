package br.com.empresa.reunioes.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "")
public class Reuniao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "")
    private String titulo;

    @Column(name = "")
    private String data;

    @Column(name = "")
    private String resumo;

    @Column(name = "")
    private String status;

    @ManyToMany
    private List<Colaborador> participantes;

    @ElementCollection
    private List<String> areas;

    @ElementCollection
    private List<String> pontosChaves;

    @OneToMany
    private List<Acao> acoes;

    private Integer totalAcoes;

}
