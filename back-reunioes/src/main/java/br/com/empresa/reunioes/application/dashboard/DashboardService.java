package br.com.empresa.reunioes.application.dashboard;

import br.com.empresa.reunioes.application.dashboard.record.Dashboard;
import br.com.empresa.reunioes.domain.enums.StatusReuniao;
import br.com.empresa.reunioes.domain.repository.AcaoRepository;
import br.com.empresa.reunioes.domain.repository.ReuniaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ReuniaoRepository reuniaoRepository;
    private final AcaoRepository acaoRepository;

    @Transactional(readOnly = true)
    public Dashboard montarDashboard() {

        String inicio = LocalDate.now().minusMonths(3).toString();

        return new Dashboard(reuniaoRepository.count(), reuniaoRepository.countByStatusReuniao(StatusReuniao.FINALIZADA),
        reuniaoRepository.countByStatusReuniaoAndDataGreaterThanEqual(StatusReuniao.FINALIZADA, inicio), acaoRepository.countByConcluida(true),
        acaoRepository.countByConcluida(false));
  }
}
