package br.com.empresa.reunioes.domain.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dashboard {

    private long totalReunioes;
    private long reunioesConcluidas;
    private long reunioesConcluidasUltimos3Meses;
    private long acoesConcluidas;
    private long acoesPendentes;
}
