package br.com.empresa.reunioes.domain.entity;

import br.com.empresa.reunioes.domain.enums.Papel;
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

    /** Identifica o colaborador no login. Unico no banco. */
    @Column(nullable = false, unique = true)
    private String email;

    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Papel papel = Papel.USUARIO;

    private Boolean monitorarReunioes;
    private String dataCadastro;

    /** Acoes das quais este colaborador e responsavel. Lado inverso: quem manda e Acao. */
    @ManyToMany(mappedBy = "responsavel")
    private List<Acao> acoes;

    @ManyToMany(mappedBy = "participantes")
    private List<Reuniao> reunioes;

}
