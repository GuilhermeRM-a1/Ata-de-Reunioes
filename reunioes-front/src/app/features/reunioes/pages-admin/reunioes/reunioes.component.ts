import { Component, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { ReuniaoStoreService } from '../../../../core/services/reuniao.service';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { ReuniaoApiDTO } from '../../../../core/models/api/reuniao-api.model';
import { AlertaService } from '../../../../core/services/alerta.service';

@Component({
  selector: 'app-reunioes',
  standalone: true,
  imports: [CommonModule, DatePipe, StatusBadgeComponent],
  templateUrl: './reunioes.component.html',
  styleUrl: './reunioes.component.scss'
})
export class ReunioesComponent implements OnInit {
  readonly reunioes = signal<ReuniaoApiDTO[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal<string | null>(null);

  reuniaoParaExcluir: ReuniaoApiDTO | null = null;

  constructor(
    private readonly reuniaoService: ReuniaoStoreService,
    private readonly router: Router,
    private readonly alerta: AlertaService
  ) {}

  ngOnInit(): void {
    this.carregarReunioes();
  }

  private carregarReunioes(): void {
    this.carregando.set(true);
    this.erro.set(null);

    this.reuniaoService.listar().subscribe({
      next: (dados: any) => { 
        this.reunioes.set(dados.content);
        this.carregando.set(false);
      },
      error: (err: any) => {
        console.error('Erro ao carregar reuniões:', err);
        this.erro.set('Não foi possível carregar as reuniões. Tente novamente em instantes.');
        this.carregando.set(false);
      }
    });
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

  abrirConfirmacaoExclusao(reuniao: ReuniaoApiDTO, event: Event): void {
    event.stopPropagation();
    this.reuniaoParaExcluir = reuniao;
  }

  cancelarExclusao(): void {
    this.reuniaoParaExcluir = null;
  }

  confirmarExclusao(): void {
    if (!this.reuniaoParaExcluir) return;

    const id = this.reuniaoParaExcluir.id;
    const titulo = this.reuniaoParaExcluir.titulo;

    this.reuniaoService.remover(id).subscribe({
      next: () => {
        this.reunioes.update(lista => lista.filter(r => r.id !== id));
        this.reuniaoParaExcluir = null;
        this.alerta.sucesso('Reunião excluída', titulo);
      },
      // O aviso de falha vem do interceptor; aqui so fechamos o modal.
      error: () => {
        this.reuniaoParaExcluir = null;
      }
    });
  }
}