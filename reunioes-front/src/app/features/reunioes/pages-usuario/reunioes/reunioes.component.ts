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
    this.router.navigate(['/usuario/reunioes/novo']);
  }

  verReuniao(id: number): void {
    this.router.navigate(['/usuario/reunioes', id]);
  }

  editarReuniao(id: number, event: Event): void {
    event.stopPropagation();
    this.router.navigate(['/usuario/reunioes', id, 'editar']);
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
        this.reunioes.update(lista => lista.filter(r => r.id !== reuniao.id));
        this.alerta.sucesso('Reunião excluída', reuniao.titulo);
      },
      error: () => {}
    });
  }
}