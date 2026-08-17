import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatusReuniao } from '../../../core/models/reuniao.model';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './status-badge.component.html',
  styleUrl: './status-badge.component.scss'
})
export class StatusBadgeComponent {
  @Input({ required: true }) status!: StatusReuniao;

  private readonly rotulos: Record<StatusReuniao, string> = {
    [StatusReuniao.RECEBIDA]: 'Recebida',
    [StatusReuniao.TRANSCREVENDO]: 'Transcrevendo',
    [StatusReuniao.ANALISANDO]: 'Analisando',
    [StatusReuniao.CONCLUIDA]: 'Concluída',
    [StatusReuniao.ERRO]: 'Erro',
  };

  get rotulo(): string {
    return this.rotulos[this.status];
  }

  get classeCss(): string {
    return `status-badge status-${this.status.toLowerCase()}`;
  }
}