import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { MdbRippleModule } from 'mdb-angular-ui-kit/ripple';
import { Acao } from '../../../../core/models';
import { AcaoService } from '../../../../core/services/acao.service';
import { AlertaService } from '../../../../core/services/alerta.service';
import { AuthService } from '../../../../core/services/auth.service';
import { DashboardService } from '../../../../core/services/dashboard.service';
import { Dashboard } from '../../../../core/models/dashboard.model';

@Component({
  selector: 'app-acoes',
  standalone: true,
  imports: [CommonModule, FormsModule, MdbRippleModule, RouterLink, RouterLinkActive],
  templateUrl: './acoes.component.html',
  styleUrls: ['./acoes.component.scss']
})
export class AcoesComponent implements OnInit {

  private readonly dashboardService = inject(DashboardService);
  private readonly acaoService = inject(AcaoService);
  private readonly alertaService = inject(AlertaService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly auth = inject(AuthService);

  readonly exibeConcluidas = this.route.snapshot.data['status'] === 'concluidas';

  readonly dashboard = signal<Dashboard | null>(null);
  readonly carregando = signal(true);

  private readonly todasAcoes = signal<Acao[]>([]);

  readonly totalPendentes = computed(() =>
    this.todasAcoes().filter(a => !a.concluida).length
  );

  readonly totalConcluidas = computed(() =>
    this.todasAcoes().filter(a => a.concluida).length
  );

  readonly acoes = computed(() =>
    this.ordenarPorPrazo(
      this.todasAcoes().filter(a => !!a.concluida === this.exibeConcluidas)
    )
  );

  ngOnInit(): void {
    this.carregarAcoes();
  }

  carregarDashboard(): void {
    this.dashboardService.buscar().subscribe({
      next: d => this.dashboard.set(d),
      error: err => console.error('Erro ao carregar dashboard', err)
    });
  }

  carregarAcoes(): void {
    this.carregando.set(true);

    this.acaoService.listarAcoes().subscribe({
      next: (dados: any) => {
        this.todasAcoes.set(
          Array.isArray(dados) ? dados : (dados?.content || dados?.itens || [])
        );

        this.carregando.set(false);
        this.carregarDashboard();
      },
      error: (err) => {
        console.error('Erro ao carregar ações', err);
        this.carregando.set(false);
      }
    });
  }

  mudarStatus(acao: Acao): void {
    if (!this.auth.isAdmin) return;

    const request = {
      titulo: acao.titulo,
      descricao: acao.descricao,
      tipo: acao.tipo,
      prazo: acao.prazo,
      concluida: !acao.concluida
    };

    this.acaoService.atualizarParcial(acao.id, request).subscribe({
      next: () => this.carregarAcoes(),
      error: (err) => console.error('Erro ao alterar status', err)
    });
  }

  async excluir(acao: Acao, event: Event): Promise<void> {
    event.stopPropagation();
    if (!this.auth.isAdmin) return;

    const confirmado = await this.alertaService.confirmar(
      'Excluir ação',
      `Tem certeza que deseja excluir "${acao.titulo}"? Essa ação não pode ser desfeita.`
    );

    if (!confirmado) return;

    this.acaoService.remover(acao.reuniaoId, acao.id).subscribe({
      next: () => {
        this.alertaService.sucesso('Ação excluída');
        this.carregarAcoes();
      },
      error: (err) => {
        console.error('Erro ao excluir ação', err);
        this.alertaService.erro('Erro ao excluir', 'Não foi possível excluir a ação.');
      }
    });
  }

  verReuniao(reuniaoId: number): void {
    if (reuniaoId) {
      this.router.navigate(['/reunioes', reuniaoId]);
    }
  }

  private ordenarPorPrazo(lista: Acao[]): Acao[] {
    return [...lista].sort((a, b) => {
      if (a.prazo === null) return 1;
      if (b.prazo === null) return -1;
      return a.prazo.localeCompare(b.prazo);
    });
  }
}