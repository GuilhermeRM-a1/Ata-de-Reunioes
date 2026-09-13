import { Component, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { ReuniaoService } from '../../../../core/services/reuniao.service';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { ReuniaoApiDTO } from '../../../../core/models/api/reuniao-api.model';

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
    private readonly reuniaoService: ReuniaoService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.carregarReunioes();
  }

  private carregarReunioes(): void {
    this.carregando.set(true);
    this.erro.set(null);

    this.reuniaoService.listar().subscribe({
      next: (dados) => {
        this.reunioes.set(dados);
        this.carregando.set(false);
      },
      error: (err) => {
        console.error('Erro ao carregar reuniões:', err);
        this.erro.set('Não foi possível carregar as reuniões. Tente novamente em instantes.');
        this.carregando.set(false);
      }
    });
  }

  novaReuniao(): void {
    this.router.navigate(['/reunioes/novo']);
  }

  verReuniao(id: number): void {
    this.router.navigate(['/reunioes', id]);
  }

  editarReuniao(id: number, event: Event): void {
    event.stopPropagation();
    this.router.navigate(['/reunioes', id, 'editar']);
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

    this.reuniaoService.deletar(id).subscribe({
      next: () => {
        this.reunioes.update(lista => lista.filter(r => r.id !== id));
        this.reuniaoParaExcluir = null;
      },
      error: (err) => {
        console.error('Erro ao excluir reunião:', err);
        this.erro.set('Não foi possível excluir a reunião. Tente novamente.');
        this.reuniaoParaExcluir = null;
      }
    });
  }
}