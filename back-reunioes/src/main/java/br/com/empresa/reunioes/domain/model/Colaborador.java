package br.com.empresa.reunioes.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table (name = "colaboradores")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class Colaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "email_colaborador", unique = true, nullable = false)
    private String email;

    @Column (name = "nome_colaborador", nullable = false, length = 150)
    private String nome;

    @Column (name = "monitorar_reunioes", nullable = false)
    private Boolean monitorarReunioes;

    @Column (name = "data_cadastro", nullable = false)
    private String dataCadastro;

}
