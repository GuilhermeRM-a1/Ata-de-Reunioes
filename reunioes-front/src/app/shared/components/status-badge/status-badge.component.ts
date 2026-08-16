import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { STATUS_LABEL, StatusReuniao } from '../../../core/models';

/**
 * Badge de status usado pela listagem e pelo detalhe. Sai como componente
 * proprio justamente para as duas telas nao duplicarem cor e rotulo.
 */
@Component({
  selector: 'app-status-badge',
  imports: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './status-badge.component.html',
  styleUrl: './status-badge.component.scss',
})
export class StatusBadgeComponent {
  readonly status = input.required<StatusReuniao>();

  /** Rotulo legivel: CONCLUIDA vira "Concluída". */
  readonly rotulo = computed(() => STATUS_LABEL[this.status()]);

  /** Uma classe por status, para o SCSS pintar cada um de um jeito. */
  readonly classe = computed(() => `status-badge--${this.status().toLowerCase()}`);
}
