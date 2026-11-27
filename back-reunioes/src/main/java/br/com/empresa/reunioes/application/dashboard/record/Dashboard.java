package br.com.empresa.reunioes.application.dashboard.record;

public record Dashboard(long totalReunioes,
                                long reunioesConcluidas,
                                long reunioesConcluidasUltimos3Meses,
                                long acoesConcluidas,
                                long acoesPendentes) {
}