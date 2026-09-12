import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Acao } from '../../../../core/models/acao.model';
import { AcaoService } from '../../../../core/services/acao.service';

@Component({
  selector: 'app-acao',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './acao.component.html',
  styleUrls: ['./acao.component.scss']
})
export class AcaoComponent implements OnInit {
  todasAcoes: Acao[] = [];
  acoesPendentes: Acao[] = [];
  acoesConcluidas: Acao[] = [];
  
  carregando: boolean = true;
  paginaAtual: number = 1;
  limite: number = 10;
  totalPaginas: number = 1;

  constructor(
    private acaoService: AcaoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carregarAcoes();
  }

  carregarAcoes(): void {
    this.carregando = true;
    
    this.acaoService.listarAcoes().subscribe({
      next: (dados: any) => {
        this.todasAcoes = Array.isArray(dados) ? dados : (dados.content || dados.itens || []);
        this.atualizarPagina();
        this.carregando = false;
      },
      error: (err) => {
        console.error('Erro ao carregar ações', err);
        this.carregando = false;
      }
    });
  }

  atualizarPagina(): void {
    const inicio = (this.paginaAtual - 1) * this.limite;
    const fim = inicio + this.limite;
    
    const acoesPaginadas = this.todasAcoes.slice(inicio, fim);
    
    this.acoesPendentes = acoesPaginadas.filter((a: Acao) => !a.concluida);
    this.acoesConcluidas = acoesPaginadas.filter((a: Acao) => a.concluida);
    
    this.totalPaginas = Math.ceil(this.todasAcoes.length / this.limite) || 1;
  }

  mudarStatus(acao: Acao): void {
    const novoStatus = !acao.concluida;

    const request = {
      titulo: acao.titulo,
      descricao: acao.descricao,
      tipo: acao.tipo,
      prazo: acao.prazo,
      status: novoStatus
    };

    this.acaoService.atualizarParcial(acao.id, request).subscribe({
      next: () => this.carregarAcoes(),
      error: (err) => console.error('Erro ao alterar status', err)
    });
  }

  verReuniao(reuniaoId: number): void {
    if (reuniaoId) {
      this.router.navigate(['/reunioes', reuniaoId]);
    }
  }

  proximaPagina(): void {
    if (this.paginaAtual < this.totalPaginas) {
      this.paginaAtual++;
      this.atualizarPagina(); // Corrigido de atualizarFatiamentoPagina para atualizarPagina
    }
  }

  paginaAnterior(): void {
    if (this.paginaAtual > 1) {
      this.paginaAtual--;
      this.atualizarPagina(); // Corrigido de atualizarFatiamentoPagina para atualizarPagina
    }
  }
}