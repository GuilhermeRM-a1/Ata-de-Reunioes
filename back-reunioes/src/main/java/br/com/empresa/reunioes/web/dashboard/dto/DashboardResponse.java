package br.com.empresa.reunioes.web.dashboard.dto;

public record DashboardResponse(long totalReunioes,
                        long reunioesConcluidas,
                        long reunioesConcluidasUltimos3Meses,
                        long acoesConcluidas,
                        long acoesPendentes) {
}
