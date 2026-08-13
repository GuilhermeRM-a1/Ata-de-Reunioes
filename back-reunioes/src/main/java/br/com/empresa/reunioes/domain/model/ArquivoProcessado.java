package br.com.empresa.reunioes.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table (name = "arquivos_processados")
@NoArgsConstructor @AllArgsConstructor
@Builder
@Getter @Setter
public class ArquivoProcessado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;
    @Column (name = "nomes_arquivo", nullable = false, length = 150)
    private String fileName;
    @Column (name = "data_processamento", nullable = false)
    private String dataProcessamento;

}
