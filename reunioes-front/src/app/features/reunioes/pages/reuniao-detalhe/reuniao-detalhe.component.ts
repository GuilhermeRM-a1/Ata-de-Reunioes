import { Component, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ReuniaoStoreService } from '../../../../core/services/reuniao-store.service';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { ReuniaoDetalhe } from '../../../../core/models/reuniao.model';

@Component({
  selector: 'app-reuniao-detalhe',
  standalone: true,
  imports: [CommonModule, DatePipe, RouterLink, StatusBadgeComponent],
  templateUrl: './reuniao-detalhe.component.html',
  styleUrl: './reuniao-detalhe.component.scss'
})
export class ReuniaoDetalheComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(ReuniaoStoreService);

  reuniao: ReuniaoDetalhe | undefined;
  reuniaoNaoEncontrada = false;
  modalExclusaoAberto = false;

  constructor() {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam ? Number(idParam) : NaN;

    // Garanta que o método buscarPorId no service retorne ReuniaoDetalhe | undefined
    this.reuniao = this.store.buscarPorId(id) as ReuniaoDetalhe | undefined;
    this.reuniaoNaoEncontrada = !this.reuniao;
  }

  get pontosChaveLista(): string[] {
    if (!this.reuniao?.pontosChave) return [];
    
    return this.reuniao.pontosChave
      .split('\n')
      .map((linha: string) => linha.trim())
      .filter((linha: string) => linha.length > 0);
  }

  abrirModalExclusao(): void {
    this.modalExclusaoAberto = true;
  }

  cancelarExclusao(): void {
    this.modalExclusaoAberto = false;
  }

  confirmarExclusao(): void {
    if (this.reuniao) {
      this.store.remover(this.reuniao.id);
      this.router.navigate(['/reunioes']);
    }
  }
}