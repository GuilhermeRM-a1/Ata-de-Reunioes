import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ReuniaoService } from '../../../../core/services/reuniao.service';
import { AlertaService } from '../../../../core/services/alerta.service';
import { AuthService } from '../../../../core/services/auth.service';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { ReuniaoApiDTO } from '../../../../core/models';

@Component({
  selector: 'app-reuniao-detalhe',
  standalone: true,
  imports: [CommonModule, DatePipe, RouterLink, StatusBadgeComponent],
  templateUrl: './reuniao-detalhe.component.html',
  styleUrl: './reuniao-detalhe.component.scss'
})
export class ReuniaoDetalheComponent implements OnInit {
  // publico de proposito: o template le auth.isAdmin para mostrar ou esconder o CRUD
  readonly auth = inject(AuthService);

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly reuniaoService = inject(ReuniaoService);
  private readonly alerta = inject(AlertaService);

  readonly reuniao = signal<ReuniaoApiDTO | undefined>(undefined);
  readonly reuniaoNaoEncontrada = signal(false);
  readonly carregando = signal(true);
  readonly modalExclusaoAberto = signal(false);
  readonly transcricaoAberta = signal(false);

  /** No back os pontos-chave ja vem como lista. */
  readonly pontosChaveLista = computed<string[]>(() => this.reuniao()?.pontosChaves ?? []);

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam ? Number(idParam) : NaN;

    if (isNaN(id)) {
      this.reuniaoNaoEncontrada.set(true);
      this.carregando.set(false);
      return;
    }

    this.reuniaoService.buscarPorId(id).subscribe({
      next: dados => {
        this.reuniao.set(dados);
        this.reuniaoNaoEncontrada.set(!dados);
        this.carregando.set(false);
      },
      error: () => {
        this.reuniaoNaoEncontrada.set(true);
        this.carregando.set(false);
      }
    });
  }

  abrirModalExclusao(): void {
    if (!this.auth.isAdmin) return;
    this.modalExclusaoAberto.set(true);
  }

  cancelarExclusao(): void {
    this.modalExclusaoAberto.set(false);
  }

  confirmarExclusao(): void {
    if (!this.auth.isAdmin) return;

    const atual = this.reuniao();
    if (!atual?.id) return;

    this.reuniaoService.remover(atual.id).subscribe({
      next: () => {
        this.modalExclusaoAberto.set(false);
        this.alerta.sucesso('Reunião excluída', atual.titulo);
        this.router.navigate(['/reunioes']);
      },
      error: err => console.error('Erro ao excluir reunião', err)
    });
  }

  alternarTranscricao(): void {
    this.transcricaoAberta.update(aberta => !aberta);
  }
}
