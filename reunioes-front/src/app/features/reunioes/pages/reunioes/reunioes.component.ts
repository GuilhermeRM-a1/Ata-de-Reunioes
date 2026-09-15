import { Component, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MdbModalService } from 'mdb-angular-ui-kit/modal';
import { ReuniaoFormComponent } from '../reuniao-form/reuniao-form.component';
import { ReuniaoStoreService } from '../../../../core/services/reuniao-store.service';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { Reuniao } from '../../../../core/models';
import { Router } from '@angular/router'; 
import { MdbModalModule } from 'mdb-angular-ui-kit/modal';

@Component({
  selector: 'app-reunioes',
  standalone: true,
  imports: [CommonModule, DatePipe, StatusBadgeComponent, MdbModalModule  ],
  templateUrl: './reunioes.component.html',
  styleUrl: './reunioes.component.scss'
})
export class ReunioesComponent {
  private readonly store = inject(ReuniaoStoreService);
  private modalService = inject(MdbModalService);
  private router = inject(Router); 
  readonly reunioes = this.store.listar;

  reuniaoParaExcluir: Reuniao | null = null;

  novaReuniao(): void {
    this.modalService.open(ReuniaoFormComponent);
  }

  verReuniao(id: number): void {
    this.router.navigate(['/reunioes', id]);
  }

  editarReuniao(id: number): void {
     this.modalService.open(ReuniaoFormComponent, {
    data: { idEditando: id }
  });
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