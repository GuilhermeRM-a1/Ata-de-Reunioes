import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

/**
 * Ponto unico de feedback visual. As telas e o interceptor chamam este
 * servico em vez de importar o SweetAlert direto — assim o estilo dos
 * alertas fica igual no sistema inteiro e trocar de biblioteca depois
 * mexe em um arquivo so.
 */
@Injectable({ providedIn: 'root' })
export class AlertaService {

  erro(titulo: string, mensagem?: string): void {
    Swal.fire({
      icon: 'error',
      title: titulo,
      text: mensagem,
      confirmButtonText: 'Entendi',
      confirmButtonColor: '#dc3545',
    });
  }

  sucesso(titulo: string, mensagem?: string): void {
    Swal.fire({
      icon: 'success',
      title: titulo,
      text: mensagem,
      timer: 2000,
      showConfirmButton: false,
    });
  }

  /**
   * Confirmacao para acao destrutiva. Devolve true so quando o usuario
   * confirma de fato — fechar no X ou no Esc conta como cancelar.
   */
  async confirmar(titulo: string, mensagem: string, textoConfirmar = 'Excluir'): Promise<boolean> {
    const resposta = await Swal.fire({
      icon: 'warning',
      title: titulo,
      text: mensagem,
      showCancelButton: true,
      confirmButtonText: textoConfirmar,
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#dc3545',
      cancelButtonColor: '#6c757d',
      reverseButtons: true,
    });

    return resposta.isConfirmed;
  }
}
