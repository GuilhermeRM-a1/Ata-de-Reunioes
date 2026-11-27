package br.com.empresa.reunioes.web.dashboard.mapper;

import br.com.empresa.reunioes.application.dashboard.record.Dashboard;
import br.com.empresa.reunioes.web.dashboard.dto.DashboardResponse;

public class DashWebMapper {

    private DashWebMapper() {}

    public static DashboardResponse dashToResponse(Dashboard dash) {
        return new DashboardResponse(dash.totalReunioes(),
                dash.reunioesConcluidas(),
                dash.reunioesConcluidasUltimos3Meses(),
                dash.acoesConcluidas(),
                dash.acoesPendentes());
    }
}
