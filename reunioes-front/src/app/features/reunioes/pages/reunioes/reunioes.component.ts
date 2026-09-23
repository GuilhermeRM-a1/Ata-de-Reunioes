import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MdbRippleModule } from 'mdb-angular-ui-kit/ripple';
import { Router } from '@angular/router';
import { ReuniaoService } from '../../../../core/services/reuniao.service';
import { AlertaService } from '../../../../core/services/alerta.service';
import { AuthService } from '../../../../core/services/auth.service';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { ReuniaoApiDTO } from '../../../../core/models';

/** Bloco de listagem: um titulo e as reunioes daquele status. */
interface GrupoReunioes {
  titulo: string;
  vazio: string;
  reunioes: ReuniaoApiDTO[];
}

@Component({
  selector: 'app-reunioes',
  standalone: true,
  imports: [CommonModule, DatePipe, StatusBadgeComponent, MdbRippleModule],
  templateUrl: './reunioes.component.html',
  styleUrl: './reunioes.component.scss'
})
export class ReunioesComponent implements OnInit {
  // publico de proposito: o template le auth.isAdmin para mostrar ou esconder o CRUD
  readonly auth = inject(AuthService);

  private readonly reuniaoService = inject(ReuniaoService);
  private readonly router = inject(Router);
  private readonly alerta = inject(AlertaService);

  readonly todasReunioes = signal<ReuniaoApiDTO[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal<string | null>(null);

  readonly paginaAtual = signal(1);
  readonly limite = 10;

  /** Separa por status e ja aplica a pagina atual. Uma fatia por grupo. */
  readonly grupos = computed<GrupoReunioes[]>(() => {
    const inicio = (this.paginaAtual() - 1) * this.limite;
    const fim = inicio + this.limite;

    return [
      {
        titulo: 'Pendentes',
        vazio: 'Nenhuma reunião pendente.',
        reunioes: this.porStatus('PENDENTE', 'asc').slice(inicio, fim)
      },
      {
        titulo: 'Em andamento',
        vazio: 'Nenhuma reunião em andamento hoje.',
        reunioes: this.porStatus('EM_ANDAMENTO', 'asc').slice(inicio, fim)
      },
      {
        titulo: 'Finalizadas',
        vazio: 'Nenhuma reunião finalizada.',
        reunioes: this.porStatus('FINALIZADA', 'desc').slice(inicio, fim)
      }
    ];
  });

  readonly totalPaginas = computed(() =>
    Math.max(
      Math.ceil(this.porStatus('PENDENTE', 'asc').length / this.limite),
      Math.ceil(this.porStatus('EM_ANDAMENTO', 'asc').length / this.limite),
      Math.ceil(this.porStatus('FINALIZADA', 'desc').length / this.limite),
      1
    )
  );

  /** True quando nenhum grupo tem linha — evita tres alertas de vazio seguidos. */
  readonly listaVazia = computed(() => this.todasReunioes().length === 0);

  ngOnInit(): void {
    this.carregarReunioes();
  }

  private porStatus(status: string, ordem: 'asc' | 'desc'): ReuniaoApiDTO[] {
    return this.todasReunioes()
      .filter(r => r.statusReuniao === status)
      .sort((a, b) =>
        ordem === 'asc' ? a.data.localeCompare(b.data) : b.data.localeCompare(a.data)
      );
  }

  private carregarReunioes(): void {
    this.carregando.set(true);
    this.erro.set(null);

    this.reuniaoService.listar().subscribe({
      next: (dados: any) => {
        // o back devolve pagina (content) em alguns ambientes e array puro em outros
        this.todasReunioes.set(Array.isArray(dados) ? dados : (dados?.content ?? []));
        this.carregando.set(false);
      },
      error: (err: any) => {
        console.error('Erro ao carregar reuniões:', err);
        this.erro.set('Não foi possível carregar as reuniões. Tente novamente em instantes.');
        this.carregando.set(false);
      }
    });
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

  novaReuniao(): void {
    if (!this.auth.isAdmin) return;
    this.router.navigate(['/reunioes/novo']);
  }

  verReuniao(id: number): void {
    this.router.navigate(['/reunioes', id]);
  }

  editarReuniao(id: number, event: Event): void {
    event.stopPropagation();
    if (!this.auth.isAdmin) return;
    this.router.navigate(['/reunioes', id, 'editar']);
  }

  async excluir(reuniao: ReuniaoApiDTO, event: Event): Promise<void> {
    event.stopPropagation();
    if (!this.auth.isAdmin) return;

    const confirmado = await this.alerta.confirmar(
      'Excluir reunião',
      `Tem certeza que deseja excluir a reunião "${reuniao.titulo}"? Essa ação não pode ser desfeita.`
    );

    if (!confirmado) return;

    this.reuniaoService.remover(reuniao.id).subscribe({
      next: () => {
        // tira da lista na hora, sem recarregar tudo do back
        this.todasReunioes.update(lista => lista.filter(r => r.id !== reuniao.id));
        this.alerta.sucesso('Reunião excluída', reuniao.titulo);
      },
      error: () => {}
    });
  }
}
