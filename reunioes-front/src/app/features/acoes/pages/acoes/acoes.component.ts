import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MdbRippleModule } from 'mdb-angular-ui-kit/ripple';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Acao } from '../../../../core/models';
import { AcaoService } from '../../../../core/services/acao.service';
import { AlertaService } from '../../../../core/services/alerta.service';
import { AuthService } from '../../../../core/services/auth.service';
import { DashboardService } from '../../../../core/services/dashboard.service';
import { Dashboard } from '../../../../core/models/dashboard.model';


@Component({
  selector: 'app-acoes',
  standalone: true,
  imports: [CommonModule, FormsModule, MdbRippleModule],
  templateUrl: './acoes.component.html',
  styleUrls: ['./acoes.component.scss']
})
export class AcoesComponent implements OnInit {

  private readonly dashboardService = inject(DashboardService);
  private readonly acaoService = inject(AcaoService);
  private readonly alertaService = inject(AlertaService);
  private readonly router = inject(Router);

  /** Público de propósito: o template lê `auth.isAdmin`. */
  readonly auth = inject(AuthService);

  readonly limite = 10;
  readonly dashboard = signal<Dashboard | null>(null);

  readonly carregando = signal(true);
  readonly paginaAtual = signal(1);
  readonly totalPaginas = signal(1);

  readonly pendentes = signal<Acao[]>([]);
  readonly concluidas = signal<Acao[]>([]);

  ngOnInit(): void {
    this.recarregar();
  }

  /** Recarrega as listas e os cards. Chamado ao abrir a tela e depois de concluir/excluir. */
  recarregar(): void {
    this.carregarListas();
    this.carregarDashboard();
  }

  carregarDashboard(): void {
    this.dashboardService.buscar().subscribe({
      next: d => this.dashboard.set(d),
      error: err => console.error('Erro ao carregar dashboard', err)
    });
  }

  /** Pede pendentes e concluídas da MESMA página e só atualiza a tela quando as duas chegam. */
  private carregarListas(): void {
    forkJoin({
      respPendentes: this.acaoService.listarPagina(false, this.paginaAtual(), this.limite),
      respConcluidas: this.acaoService.listarPagina(true, this.paginaAtual(), this.limite)
    }).subscribe({
      next: ({ respPendentes, respConcluidas }) => {
        const total = Math.max(respPendentes.totalPages, respConcluidas.totalPages, 1);

        // a página atual deixou de existir (excluiu o último item dela): volta e busca de novo
        if (this.paginaAtual() > total) {
          this.paginaAtual.set(total);
          this.carregarListas();
          return;
        }

        this.pendentes.set(respPendentes.content);
        this.concluidas.set(respConcluidas.content);
        this.totalPaginas.set(total);
        this.carregando.set(false);
      },
      error: err => {
        console.error('Erro ao carregar ações', err);
        this.carregando.set(false);
      }
    });
  }

  proximaPagina(): void {
    if (this.paginaAtual() < this.totalPaginas()) {
      this.paginaAtual.update(p => p + 1);
      this.carregarListas();
    }
  }

  paginaAnterior(): void {
    if (this.paginaAtual() > 1) {
      this.paginaAtual.update(p => p - 1);
      this.carregarListas();
    }
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
      next: () => this.recarregar(),
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
        this.recarregar();
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
}