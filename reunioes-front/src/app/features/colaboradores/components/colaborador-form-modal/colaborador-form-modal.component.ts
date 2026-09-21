import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MdbModalRef } from 'mdb-angular-ui-kit/modal';
import { MdbFormsModule } from 'mdb-angular-ui-kit/forms';
import { MdbRippleModule } from 'mdb-angular-ui-kit/ripple';

import { AlertaService } from '../../../../core/services/alerta.service';
import { Papel } from '../../../../core/services/auth.service';
import { Colaborador } from '../../../../core/models';

/** O que o modal devolve quando o formulario e salvo. */
export interface ResultadoFormularioColaborador {
  edicaoId: number | null;
  dados: Colaborador;
  senha: string;
}

/**
 * Formulario de colaborador em modal (MdbModalService).
 * Nao fala com a API: devolve os dados e quem abriu decide criar ou atualizar.
 */
@Component({
  selector: 'app-colaborador-form-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, MdbFormsModule, MdbRippleModule],
  templateUrl: './colaborador-form-modal.component.html'
})
export class ColaboradorFormModalComponent {
  readonly modalRef = inject(MdbModalRef<ColaboradorFormModalComponent>);

  private readonly alerta = inject(AlertaService);

  /** null = formulario em modo "novo cadastro". Vem do `data` do modal. */
  edicaoId: number | null = null;

  inputNome = '';
  inputEmail = '';
  inputSenha = '';
  inputMonitorarReunioes = false;
  inputPapel: Papel = 'USUARIO';

  readonly papeis: Papel[] = ['ADMIN', 'USUARIO'];

  /** Chamado por quem abre o modal para preencher os campos na edicao. */
  preencher(colaborador: Colaborador): void {
    this.edicaoId = colaborador.id ?? null;
    this.inputNome = colaborador.nome;
    this.inputEmail = colaborador.email;
    this.inputMonitorarReunioes = colaborador.monitorarReunioes;
    this.inputPapel = colaborador.papel;
  }

  rotuloPapel(papel: Papel): string {
    return papel === 'ADMIN' ? 'Administrador' : 'Usuário';
  }

  cancelar(): void {
    this.modalRef.close();
  }

  salvar(): void {
    if (!this.formularioValido()) {
      return;
    }

    const dados: Colaborador = {
      nome: this.inputNome.trim(),
      email: this.inputEmail.trim(),
      monitorarReunioes: this.inputMonitorarReunioes,
      papel: this.inputPapel,
    };

    const resultado: ResultadoFormularioColaborador = {
      edicaoId: this.edicaoId,
      dados,
      senha: this.inputSenha,
    };

    this.modalRef.close(resultado);
  }

  private formularioValido(): boolean {
    if (this.inputNome.trim() === '') {
      this.alerta.erro('Nome obrigatório', 'Informe o nome do colaborador.');
      return false;
    }

    if (this.inputEmail.trim() === '') {
      this.alerta.erro('E-mail obrigatório', 'Informe o e-mail do colaborador.');
      return false;
    }

    if (this.edicaoId === null && this.inputSenha.trim() === '') {
      this.alerta.erro('Senha obrigatória', 'Informe uma senha para o novo colaborador.');
      return false;
    }

    return true;
  }
}
