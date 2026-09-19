import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { ReuniaoStoreService } from '../../../../core/services/reuniao.service';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { ReuniaoApiDTO } from '../../../../core/models/api/reuniao-api.model';
import { AlertaService } from '../../../../core/services/alerta.service';

type StatusReuniaoAgenda = 'PENDENTE' | 'EM_ANDAMENTO' | 'FINALIZADA';

@Component({
  selector: 'app-reunioes',
  standalone: true,
  imports: [CommonModule, DatePipe, StatusBadgeComponent],
  templateUrl: './reunioes.component.html',
  styleUrl: './reunioes.component.scss'
})
export class ReunioesComponent implements OnInit {
  todasReunioes: ReuniaoApiDTO[] = [];
  reunioesPendentes: ReuniaoApiDTO[] = [];
  reunioesEmAndamento: ReuniaoApiDTO[] = [];
  reunioesFinalizadas: ReuniaoApiDTO[] = [];

  carregando = true;
  erro: string | null = null;

  paginaAtual = 1;
  limite = 10;
  totalPaginas = 1;

  constructor(
    private readonly reuniaoService: ReuniaoStoreService,
    private readonly router: Router,
    private readonly alerta: AlertaService
  ) {}

  ngOnInit(): void {
    this.carregarReunioes();
  }

  private carregarReunioes(): void {
    this.carregando = true;
    this.erro = null;

    this.reuniaoService.listar().subscribe({
      next: (dados: any) => {
        this.todasReunioes = dados.content;
        this.atualizarPagina();
        this.carregando = false;
      },
      error: (err: any) => {
        console.error('Erro ao carregar reuniões:', err);
        this.erro = 'Não foi possível carregar as reuniões. Tente novamente em instantes.';
        this.carregando = false;
      }
    });
  }

  /**
   * Simulacao: o backend ainda nao tem o campo statusReuniao (Pendente/Em
   * andamento/Finalizada) separado do status de transcricao. Ate isso
   * existir na API, derivamos comparando a data da reuniao com hoje.
   */
  private obterStatusAgenda(reuniao: ReuniaoApiDTO): StatusReuniaoAgenda {
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);

    const dataReuniao = new Date(reuniao.data);
    dataReuniao.setHours(0, 0, 0, 0);

    if (dataReuniao.getTime() > hoje.getTime()) return 'PENDENTE';
    if (dataReuniao.getTime() === hoje.getTime()) return 'EM_ANDAMENTO';
    return 'FINALIZADA';
  }

  atualizarPagina(): void {
    const pendentes = this.todasReunioes
      .filter(r => this.obterStatusAgenda(r) === 'PENDENTE')
      .sort((a, b) => a.data.localeCompare(b.data));

    const emAndamento = this.todasReunioes
      .filter(r => this.obterStatusAgenda(r) === 'EM_ANDAMENTO')
      .sort((a, b) => a.data.localeCompare(b.data));

    const finalizadas = this.todasReunioes
      .filter(r => this.obterStatusAgenda(r) === 'FINALIZADA')
      .sort((a, b) => b.data.localeCompare(a.data));

    const inicio = (this.paginaAtual - 1) * this.limite;
    const fim = inicio + this.limite;

    this.reunioesPendentes = pendentes.slice(inicio, fim);
    this.reunioesEmAndamento = emAndamento.slice(inicio, fim);
    this.reunioesFinalizadas = finalizadas.slice(inicio, fim);

    this.totalPaginas = Math.max(
      Math.ceil(pendentes.length / this.limite),
      Math.ceil(emAndamento.length / this.limite),
      Math.ceil(finalizadas.length / this.limite),
      1
    );
  }

  proximaPagina(): void {
    if (this.paginaAtual < this.totalPaginas) {
      this.paginaAtual++;
      this.atualizarPagina();
    }
  }

  paginaAnterior(): void {
    if (this.paginaAtual > 1) {
      this.paginaAtual--;
      this.atualizarPagina();
    }
  }

  novaReuniao(): void {
    this.router.navigate(['/admin/reunioes/novo']);
  }

  verReuniao(id: number): void {
    this.router.navigate(['/admin/reunioes', id]);
  }

  editarReuniao(id: number, event: Event): void {
    event.stopPropagation();
    this.router.navigate(['/admin/reunioes', id, 'editar']);
  }

  async excluir(reuniao: ReuniaoApiDTO, event: Event): Promise<void> {
    event.stopPropagation();

    const confirmado = await this.alerta.confirmar(
      'Excluir reunião',
      `Tem certeza que deseja excluir a reunião "${reuniao.titulo}"? Essa ação não pode ser desfeita.`
    );

    if (!confirmado) return;

    this.reuniaoService.remover(reuniao.id).subscribe({
      next: () => {
        this.alerta.sucesso('Reunião excluída', reuniao.titulo);
        this.carregarReunioes();
      },
      error: () => {}
    });
  }
}