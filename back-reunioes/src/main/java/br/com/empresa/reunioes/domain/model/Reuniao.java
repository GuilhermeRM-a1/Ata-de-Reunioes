package br.com.empresa.reunioes.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table (name = "reunioes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class Reuniao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo_reuniao", nullable = false, length = 150)
    private String titulo;

    @Column(name = "data_reuniao", nullable = false)
    private String data;

    @Column(name = "resumo_reuniao", columnDefinition = "TEXT", nullable = false)
    private String resumo;

    @Column(name = "status_reuniao", nullable = false)
    private StatusReuniao status;

    @ManyToMany
    @JoinTable(
            name = "reuniao_participantes",
            joinColumns = @JoinColumn(name = "reuniao_id"),
            inverseJoinColumns = @JoinColumn(name = "colaborador_id")
    )
    private List<Colaborador> participantes;

    @ElementCollection
    @CollectionTable(name = "reuniao_areas", joinColumns = @JoinColumn(name = "reuniao_id"))
    @Column(name = "area")
    private List<String> areas;

    @Column(name = "total_acoes", nullable = false)
    private Integer totalAcoes;

}
