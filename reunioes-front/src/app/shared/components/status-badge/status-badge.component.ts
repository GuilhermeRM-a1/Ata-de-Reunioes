import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatusReuniao, STATUS_REUNIAO_LABEL, STATUS_TRANSCRICAO_LABEL, StatusTranscrição } from '../../../core/models';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './status-badge.component.html',
  styleUrl: './status-badge.component.scss'
})
export class StatusBadgeComponent {
  @Input({ required: true }) status!: StatusReuniao | StatusTranscrição;

  @Input({ required: true }) tipo!: 'reuniao' | 'transcricao';

  get rotulo(): string {
    if (!this.status) {
      return 'Carregando...';
    }

    if (this.tipo === 'reuniao') {
      return STATUS_REUNIAO_LABEL[this.status as StatusReuniao];
    }
    return STATUS_TRANSCRICAO_LABEL[this.status as StatusTranscrição];
  }

  get classeCss(): string {
    const classeBase = 'badge rounded-pill'; 

    if (!this.status) {
      return `${classeBase} bg-secondary`; 
    }

    switch (this.status) {
      case 'FINALIZADA':
      case 'CONCLUIDA':
        return `${classeBase} bg-success`;
        
      case 'PENDENTE':
        return `${classeBase} bg-danger `; 
        
      case 'EM_ANDAMENTO':
      case 'TRANSCREVENDO':
      case 'ANALISANDO':
        return `${classeBase} bg-info`;
        
      case 'RECEBIDA':
        return `${classeBase} bg-primary`;
        
      case 'ERRO':
        return `${classeBase} bg-danger`;
        
      default:
        return `${classeBase} bg-secondary`;
    }
  }
}