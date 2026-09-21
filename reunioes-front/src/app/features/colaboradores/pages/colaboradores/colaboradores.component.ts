import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MdbModalModule, MdbModalService } from 'mdb-angular-ui-kit/modal';
import { MdbRippleModule } from 'mdb-angular-ui-kit/ripple';

import { ColaboradorService } from '../../../../core/services/colaborador.service';
import { AlertaService } from '../../../../core/services/alerta.service';
import { AuthService, Papel } from '../../../../core/services/auth.service';
import { Colaborador } from '../../../../core/models';
import {
  ColaboradorFormModalComponent,
  ResultadoFormularioColaborador,
} from '../../components/colaborador-form-modal/colaborador-form-modal.component';

@Component({
  selector: 'app-colaboradores',
  standalone: true,
  imports: [CommonModule, MdbModalModule, MdbRippleModule],
  templateUrl: './colaboradores.component.html',
  styleUrl: './colaboradores.component.scss',
})
export class ColaboradoresComponent implements OnInit {

  private readonly colaboradorService = inject(ColaboradorService);
  private readonly alerta = inject(AlertaService);
  private readonly router = inject(Router);
  private readonly modalService = inject(MdbModalService);

  /** Publico: o template esconde a tela inteira para quem nao e admin. */
  protected readonly auth = inject(AuthService);

  readonly colaboradores = signal<Colaborador[]>([]);
  readonly carregando = signal(false);

  ngOnInit(): void {
    // Tela exclusiva de admin: quem nao for, volta para as reunioes.
    if (!this.auth.isAdmin) {
      this.router.navigate(['/reunioes']);
      return;
    }

    this.listar();
  }

  listar(): void {
    this.carregando.set(true);

    this.colaboradorService.listar().subscribe({
      next: (resposta) => {
        this.colaboradores.set(resposta);
        this.carregando.set(false);
      },
      error: () => {
        this.carregando.set(false);
        this.alerta.erro(
          'Não foi possível carregar',
          'A lista de colaboradores não pôde ser carregada. Tente novamente.',
        );
      },
    });
  }

  /** Abre o formulario vazio: a tabela continua visivel atras do modal. */
  novoColaborador(): void {
    this.abrirFormulario(null);
  }

  editar(colaborador: Colaborador): void {
    this.abrirFormulario(colaborador);
  }

  async remover(colaborador: Colaborador): Promise<void> {
    if (colaborador.id === undefined) {
      this.alerta.erro('Colaborador sem id', 'Não é possível excluir este registro.');
      return;
    }

    const confirmado = await this.alerta.confirmar(
      'Excluir colaborador?',
      `${colaborador.nome} será removido permanentemente.`,
    );

    if (!confirmado) {
      return;
    }

    this.colaboradorService.deletar(colaborador.id).subscribe({
      next: () => {
        this.alerta.sucesso('Colaborador excluído');
        this.listar();
      },
      error: () => {
        this.alerta.erro('Erro ao excluir', 'O colaborador não pôde ser removido.');
      },
    });
  }

  rotuloPapel(papel: Papel): string {
    return papel === 'ADMIN' ? 'Administrador' : 'Usuário';
  }

  private abrirFormulario(colaborador: Colaborador | null): void {
    const modalRef = this.modalService.open(ColaboradorFormModalComponent, {
      modalClass: 'modal-dialog-centered modal-lg',
    });

    if (colaborador) {
      modalRef.component.preencher(colaborador);
    }

    modalRef.onClose.subscribe((resultado: ResultadoFormularioColaborador | undefined) => {
      if (!resultado) {
        return;
      }

      if (resultado.edicaoId === null) {
        // A senha so existe no cadastro: o back a exige no POST e nunca a devolve.
        this.criar({ ...resultado.dados, senha: resultado.senha });
        return;
      }

      this.atualizar(resultado.edicaoId, resultado.dados);
    });
  }

  private criar(dados: Colaborador): void {
    this.colaboradorService.save(dados).subscribe({
      next: () => {
        this.alerta.sucesso('Colaborador cadastrado');
        this.listar();
      },
      error: (erro) => {
        this.alerta.erro('Erro ao cadastrar', this.mensagemDoErro(erro));
      },
    });
  }

  /**
   * Usa PATCH e nao PUT. O PUT cai no ColaboradorRequest, que exige senha —
   * e editar o nome de alguem nao deveria obrigar a redefinir a senha dele.
   * No PATCH, campo ausente significa "nao mexer".
   */
  private atualizar(id: number, dados: Colaborador): void {
    this.colaboradorService.atualizarParcial(id, dados).subscribe({
      next: () => {
        this.alerta.sucesso('Colaborador atualizado');
        this.listar();
      },
      error: (erro) => {
        this.alerta.erro('Erro ao atualizar', this.mensagemDoErro(erro));
      },
    });
  }

  /**
   * O back responde no formato ProblemDetail e lista o que reprovou em
   * "campos". Mostrar isso evita o "verifique os dados" que nao diz nada.
   */
  private mensagemDoErro(erro: any): string {
    const campos = erro?.error?.campos;

    if (Array.isArray(campos) && campos.length > 0) {
      return campos.map((c: any) => c.mensagem).join(' ');
    }

    return erro?.error?.detail ?? 'Verifique os dados e tente novamente.';
  }
}
