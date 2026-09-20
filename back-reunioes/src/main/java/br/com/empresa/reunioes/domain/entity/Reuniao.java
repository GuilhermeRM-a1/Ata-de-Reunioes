package br.com.empresa.reunioes.domain.entity;

import br.com.empresa.reunioes.domain.enums.StatusReuniao;
import br.com.empresa.reunioes.domain.enums.StatusTranscricao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "reuniao")
public class Reuniao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "data")
    private String data;

    /** Resumo executivo vindo da IA. E o texto que entra no relatorio. */
    @Column(name = "resumo")
    private String resumo;

    /** Transcricao pura vinda da IA. Guardada para consulta, fora do relatorio. */
    @Column(name = "transcricao")
    private String transcricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_transcricao")
    private StatusTranscricao statusTranscricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_reuniao")
    private StatusReuniao statusReuniao;

    @ManyToMany(mappedBy = "reunioes")
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
    @OneToMany(mappedBy = "reuniao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Acao> acoes;

    @Column(name = "total_acoes")
    private Integer totalAcoes;

}
