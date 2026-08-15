package br.com.empresa.reunioes.domain.model;

import br.com.empresa.reunioes.domain.enums.StatusReuniao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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
    private List<Colaborador> participantes;

    @ElementCollection
    private List<String> areas;

    @ElementCollection
    private List<String> pontosChaves;

    @OneToMany
    private List<Acao> acoes;

    private Integer totalAcoes;

}
