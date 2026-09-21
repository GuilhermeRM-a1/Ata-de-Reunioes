import { Component, inject } from '@angular/core';
import { MdbModalRef } from 'mdb-angular-ui-kit/modal';
import { MdbRippleModule } from 'mdb-angular-ui-kit/ripple';

/**
 * Modal de confirmacao aberto pelo MdbModalService.
 * Fecha com `true` quando o usuario confirma e com `false` quando desiste.
 */
@Component({
  selector: 'app-confirmacao-modal',
  standalone: true,
  imports: [MdbRippleModule],
  templateUrl: './confirmacao-modal.component.html'
})
export class ConfirmacaoModalComponent {
  readonly modalRef = inject(MdbModalRef<ConfirmacaoModalComponent>);

  /** Preenchidos pelo `data` do MdbModalConfig. */
  titulo = 'Confirmar';
  mensagem = '';
  destaque = '';
  complemento = '';
  rotuloConfirmar = 'Confirmar';
  rotuloCancelar = 'Cancelar';

  cancelar(): void {
    this.modalRef.close(false);
  }

  confirmar(): void {
    this.modalRef.close(true);
  }
}
