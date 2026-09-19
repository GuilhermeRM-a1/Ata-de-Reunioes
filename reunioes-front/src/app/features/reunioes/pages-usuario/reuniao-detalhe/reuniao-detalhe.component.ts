import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ReuniaoStoreService } from '../../../../core/services/reuniao.service';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';
import { ReuniaoApiDTO } from '../../../../core/models/api/reuniao-api.model'; // Ajuste para o seu DTO da API

@Component({
  selector: 'app-reuniao-detalhe',
  standalone: true,
  imports: [CommonModule, DatePipe, RouterLink, StatusBadgeComponent],
  templateUrl: './reuniao-detalhe.component.html',
  styleUrl: './reuniao-detalhe.component.scss'
})
export class ReuniaoDetalheComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(ReuniaoStoreService);

  mensagemSucessoVisivel = false;
  reuniao: ReuniaoApiDTO | undefined; // Ajustado para o DTO do backend
  reuniaoNaoEncontrada = false;
  modalExclusaoAberto = false;
  transcricaoAberta = false;
  carregando = true; // Útil para exibir um indicador de loading no HTML se quiser

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam ? Number(idParam) : NaN;

    if (isNaN(id)) {
      this.reuniaoNaoEncontrada = true;
      this.carregando = false;
      return;
    }

    // Como agora é HTTP, usamos subscribe para escutar a resposta da API
    this.store.buscarPorId(id).subscribe({
      next: (dados) => {
        this.reuniao = dados;
        this.reuniaoNaoEncontrada = !dados;
        this.carregando = false;
      },
      error: () => {
        this.reuniaoNaoEncontrada = true;
        this.carregando = false;
      }
    });
  }


  
  get pontosChaveLista(): string[] {
    // Como no backend já é uma lista (array), basta retornar ela ou um array vazio
    return this.reuniao?.pontosChaves || [];
  }

  abrirModalExclusao(): void {
    this.modalExclusaoAberto = true;
  }

  cancelarExclusao(): void {
    this.modalExclusaoAberto = false;
  }

  confirmarExclusao(): void {
    if (this.reuniao?.id) {
      this.store.remover(this.reuniao.id).subscribe({
        next: () => {
          this.router.navigate(['/admin/reunioes']);
        },
        error: (err) => console.error('Erro ao excluir reunião', err)
      });
    }
  }

  alternarTranscricao(): void {
    this.transcricaoAberta = !this.transcricaoAberta;
  }

  salvarAlteracoes(): void {
    if (this.reuniao) { 
      this.mensagemSucessoVisivel = true;
      
      setTimeout(() => {
        this.mensagemSucessoVisivel = false;
        this.router.navigate([`/admin/reunioes/${this.reuniao?.id}`]);
      }, 1500);
    }
  }
}