import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MdbRippleModule } from 'mdb-angular-ui-kit/ripple';
import { Router } from '@angular/router';
import { Acao } from '../../../../core/models';
import { AcaoService } from '../../../../core/services/acao.service';
import { AlertaService } from '../../../../core/services/alerta.service';
import { AuthService } from '../../../../core/services/auth.service';

/**
 * Tela unica de acoes. Os controles de edicao e exclusao so aparecem
 * para ADMIN — e os metodos tambem barram quem nao e admin, porque
 * esconder o botao no template nao impede ninguem de chamar o metodo.
 */
@Component({
  selector: 'app-acoes',
  standalone: true,
  imports: [CommonModule, FormsModule, MdbRippleModule],
  templateUrl: './acoes.component.html',
  styleUrls: ['./acoes.component.scss']
})
export class AcoesComponent implements OnInit {

  private readonly acaoService = inject(AcaoService);
  private readonly alertaService = inject(AlertaService);
  private readonly router = inject(Router);

  /** Publico de proposito: o template le `auth.isAdmin`. */
  readonly auth = inject(AuthService);

  readonly carregando = signal(true);
  readonly paginaAtual = signal(1);
  readonly limite = signal(10);

  private readonly todasAcoes = signal<Acao[]>([]);

  private readonly pendentes = computed(() =>
    this.ordenarPorPrazo(this.todasAcoes().filter(a => !a.concluida))
  );

  private readonly concluidas = computed(() =>
    this.ordenarPorPrazo(this.todasAcoes().filter(a => a.concluida))
  );

  readonly acoesPendentes = computed(() => this.fatiarPagina(this.pendentes()));
  readonly acoesConcluidas = computed(() => this.fatiarPagina(this.concluidas()));

  readonly totalPaginas = computed(() => Math.max(
    Math.ceil(this.pendentes().length / this.limite()),
    Math.ceil(this.concluidas().length / this.limite()),
    1
  ));

  ngOnInit(): void {
    this.carregarAcoes();
  }

  carregarAcoes(): void {
    this.carregando.set(true);

    this.acaoService.listarAcoes().subscribe({
      next: (dados: any) => {
        this.todasAcoes.set(
          Array.isArray(dados) ? dados : (dados?.content || dados?.itens || [])
        );
        this.carregando.set(false);
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

  proximaPagina(): void {
    if (this.paginaAtual() < this.totalPaginas()) {
      this.paginaAtual.update(p => p + 1);
    }
  }

  paginaAnterior(): void {
    if (this.paginaAtual() > 1) {
      this.paginaAtual.update(p => p - 1);
    }
  }

  private ordenarPorPrazo(lista: Acao[]): Acao[] {
    return [...lista].sort((a, b) => {
      if (a.prazo === null) return 1;
      if (b.prazo === null) return -1;
      return a.prazo.localeCompare(b.prazo);
    });
  }

  private fatiarPagina(lista: Acao[]): Acao[] {
    const inicio = (this.paginaAtual() - 1) * this.limite();
    return lista.slice(inicio, inicio + this.limite());
  }
}
