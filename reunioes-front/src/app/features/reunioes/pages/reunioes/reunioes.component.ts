import { Component, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { ReuniaoStoreService } from '../../../../core/services/reuniao-store.service';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { Reuniao } from '../../../../core/models/reuniao.model';

@Component({
  selector: 'app-reunioes',
  standalone: true,
  imports: [CommonModule, DatePipe, StatusBadgeComponent],
  templateUrl: './reunioes.component.html',
  styleUrl: './reunioes.component.scss'
})
export class ReunioesComponent {
  private readonly store = inject(ReuniaoStoreService);
  private readonly router = inject(Router);

  readonly reunioes = this.store.reunioes;

  reuniaoParaExcluir: Reuniao | null = null;

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

  abrirConfirmacaoExclusao(reuniao: Reuniao, event: Event): void {
    event.stopPropagation();
    this.reuniaoParaExcluir = reuniao;
  }

  cancelarExclusao(): void {
    this.reuniaoParaExcluir = null;
  }

  confirmarExclusao(): void {
    if (this.reuniaoParaExcluir) {
      this.store.remover(this.reuniaoParaExcluir.id);
      this.reuniaoParaExcluir = null;
    }
  }

  resumoTruncado(resumo: string): string {
    const limite = 80;
    return resumo.length > limite ? resumo.slice(0, limite) + '…' : resumo;
  }
}