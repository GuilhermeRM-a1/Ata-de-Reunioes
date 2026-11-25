package br.com.empresa.reunioes.web.dashboard;

import br.com.empresa.reunioes.application.dashboard.DashboardService;
import br.com.empresa.reunioes.domain.entity.Dashboard;
import br.com.empresa.reunioes.web.dashboard.dto.DashboardResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Indicadores gerais")
public class DashboardController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    @GetMapping
    public ResponseEntity<DashboardResponse> montarDashboard() {
        Dashboard dash = dashboardService.montarDashboard();
        DashboardResponse dto = new DashboardResponse(dash.getTotalReunioes(), dash.getReunioesConcluidas(),
                dash.getReunioesConcluidasUltimos3Meses(),
                dash.getAcoesConcluidas(), dash.getAcoesPendentes());
        return ResponseEntity.ok(dto);
    }
}
