import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { ColaboradorService } from '../../../../core/services/colaborador.service';
import { AlertaService } from '../../../../core/services/alerta.service';
import { AuthService, Papel } from '../../../../core/services/auth.service';
import { Colaborador } from '../../../../core/models';

@Component({
  selector: 'app-colaboradores',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './colaboradores.component.html',
  styleUrl: './colaboradores.component.scss',
})
export class ColaboradoresComponent implements OnInit {

  private readonly colaboradorService = inject(ColaboradorService);
  private readonly alerta = inject(AlertaService);
  private readonly router = inject(Router);

  /** Publico: o template esconde a tela inteira para quem nao e admin. */
  protected readonly auth = inject(AuthService);

  readonly colaboradores = signal<Colaborador[]>([]);
  readonly carregando = signal(false);

  /** null = formulario em modo "novo cadastro". */
  edicaoId: number | null = null;

  inputNome = '';
  inputEmail = '';
  inputSenha = '';
  inputMonitorarReunioes = false;
  inputPapel: Papel = 'USUARIO';

  readonly papeis: Papel[] = ['ADMIN', 'USUARIO'];

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

  editar(colaborador: Colaborador): void {
    this.edicaoId = colaborador.id ?? null;
    this.inputNome = colaborador.nome;
    this.inputEmail = colaborador.email;
    this.inputMonitorarReunioes = colaborador.monitorarReunioes;
    this.inputPapel = colaborador.papel;
  }

  cancelarEdicao(): void {
    this.limparFormulario();
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

    if (this.edicaoId === null) {
      // A senha so existe no cadastro: o back a exige no POST e nunca a devolve.
      this.criar({ ...dados, senha: this.inputSenha });
      return;
    }

    this.atualizar(this.edicaoId, dados);
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
        this.limparFormulario();
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

  private criar(dados: Colaborador): void {
    this.colaboradorService.save(dados).subscribe({
      next: () => {
        this.alerta.sucesso('Colaborador cadastrado');
        this.limparFormulario();
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
        this.limparFormulario();
        this.listar();
      },
      error: (erro) => {
        this.alerta.erro('Erro ao atualizar', this.mensagemDoErro(erro));
      },
    });
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

  private limparFormulario(): void {
    this.edicaoId = null;
    this.inputNome = '';
    this.inputEmail = '';
    this.inputSenha = '';
    this.inputMonitorarReunioes = false;
    this.inputPapel = 'USUARIO';
  }
}
