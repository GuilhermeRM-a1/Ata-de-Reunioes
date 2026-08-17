import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReuniaoStoreService } from '../../../../core/mock/reuniao-store.service';
import { Router } from '@angular/router';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-reuniao-lista',
  standalone: true,
  imports: [CommonModule, StatusBadgeComponent],
  templateUrl: './reuniao-lista.component.html',
  styleUrl: './reuniao-lista.component.scss',
})
export class ReuniaoListaComponent {
  reunioes;

  constructor(
    private store: ReuniaoStoreService,
    private router: Router,
  ) {
    this.reunioes = this.store.listar;
  }

  //Redireciona para o formulário vazio
  irParaCriar(): void {
    this.router.navigate(['/reunioes/novo']);
  }

  editar(id: number): void {
    this.router.navigate([`/reunioes/editar/${id}`]);
  }

  verDetalhes(id: number): void {
    this.router.navigate([`/reunioes/${id}`]);
  }

  excluir(id: number): void {
    const confirmacao = window.confirm(
      'Tem certeza que deseja excluir esta reunião?',
    );
    if (confirmacao) {
      const sucesso = this.store.remover(id);
      if (!sucesso) {
        window.alert('Erro ao excluir a reunião. Por favor, tente novamente.');
      }
    }
  }
}
