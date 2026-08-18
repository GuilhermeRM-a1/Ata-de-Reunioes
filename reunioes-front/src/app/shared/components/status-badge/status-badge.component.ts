import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatusReuniao, STATUS_LABEL } from '../../../core/models';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './status-badge.component.html',
  styleUrl: './status-badge.component.scss'
})
export class StatusBadgeComponent {
  @Input({ required: true }) status!: StatusReuniao;

  get rotulo(): string {
    return STATUS_LABEL[this.status];
  }

  get classeCss(): string {
    return `status-badge status-badge--${this.status.toLowerCase()}`;
  }
}