import { Component, inject, input, OnInit } from '@angular/core';
import { ColaboradorServiceService } from '../../../../core/services/colaborador-service.service';
import { Colaborador } from '../../../../core/models';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-colaboradores',
  imports: [CommonModule, FormsModule],
  templateUrl: './colaboradores.component.html',
  styleUrl: './colaboradores.component.scss',
})
export class ColaboradoresComponent implements OnInit {


  private colaboradorService = inject(ColaboradorServiceService);

  inputId: number | null = null;

  inputEmail: string = '';
  inputNome: string = '';
  inputMonitorarReunioes: boolean = false;

  colaboradores: Colaborador[] = [];
  ngOnInit(): void {
    this.listar();
  }

  colaboradorSelecionado: Colaborador | null = null;

  deletar() {
    if (this.inputId === null) {
      alert('Por favor, informe o ID do colaborador que deseja deletar!');
      return;
    }

    const confirmar = confirm(
      `Tem certeza que deseja excluir o colaborador de ID ${this.inputId}?`,
    );
    if (!confirmar) {
      return;
    }

    const id: number = this.inputId;

    this.colaboradorService.deletar(id).subscribe({
      next: () => {
        console.log('Colaborador deletado com sucesso!');
        alert('Colaborador excluído com sucesso!');

        this.inputId = null;
      },
      error: (erro) => {
        console.error('Erro ao deletar colaborador:', erro);
        alert('Erro ao excluir. Verifique se o ID realmente existe.');
      },
    });
  }

  atualizarParcial() {
    if (this.inputId === null) {
      alert('Por favor, informe o ID do colaborador que deseja atualizar!');
      return;
    }

    const id: number = this.inputId;

    const dadosParciais: Partial<Colaborador> = {};

    if (this.inputNome.trim() !== '') {
      dadosParciais.nome = this.inputNome;
    }

    if (this.inputEmail.trim() !== '') {
      dadosParciais.email = this.inputEmail;
    }

    dadosParciais.monitorarReunioes = this.inputMonitorarReunioes;

    if (Object.keys(dadosParciais).length === 0) {
      alert('Preencha ao menos um campo para atualizar!');
      return;
    }
    this.colaboradorService.atualizarParcial(id, dadosParciais).subscribe({
      next: (resposta) => {
        console.log(
          'Colaborador atualizado parcialmente com sucesso!',
          resposta,
        );
        alert('Atualização parcial realizada com sucesso!');
      },
      error: (erro) => {
        console.error('Erro ao atualizar parcialmente:', erro);
        alert('Erro ao atualizar. Verifique o ID.');
      },
    });
  }

  atualizar() {
    if (this.inputId === null) {
      alert('Por favor, informe o ID do colaborador que deseja atualizar!');
      return;
    }
    const colaboradorAtualizado: Colaborador = {
      nome: this.inputNome,
      email: this.inputEmail,
      monitorarReunioes: this.inputMonitorarReunioes,
    };

    this.colaboradorService
      .atualizar(this.inputId, colaboradorAtualizado)
      .subscribe({
        next: (resposta) => {
          console.log('Colaborador atualizado com sucesso!', resposta);
          alert('Atualizado com sucesso!');
        },
        error: (erro) => {
          console.error('Erro ao atualizar colaborador:', erro);
          alert('Erro ao atualizar. Verifique se o ID existe.');
        },
      });
  }

  buscarPorId(id: number) {
    this.colaboradorService.buscarPorId(id).subscribe({
      next: (resposta) => {
        this.colaboradorSelecionado = resposta;
        console.log('Colaborador encontrado:', this.colaboradorSelecionado);
      },
      error: (erro) => {
        console.error('Erro ao buscar colaborador:', erro);
        alert('Colaborador não encontrado.');
      },
    });
  }

  listar() {
    this.colaboradorService.listar().subscribe({
      next: (resposta) => {
        console.log('Colaboradores carregados:', this.colaboradores);
        this.colaboradores = resposta;
      },
      error: (erro) => {
        console.error('Erro ao carregar colaboradores:', erro);
        alert('Não foi possível carregar a lista de colaboradores.');
      },
    });
  }

  save() {
    const novoColaborador: Colaborador = {
      email: this.inputEmail,
      nome: this.inputNome,
      monitorarReunioes: this.inputMonitorarReunioes,
    };

    this.colaboradorService.save(novoColaborador).subscribe({
      next: (resposta) => {
        alert('Cadastrado com sucesso!');
        console.log('Colaborador Cadastrado com sucesso', resposta);
        this.inputEmail = '';
        this.inputMonitorarReunioes = false;
        this.inputNome = '';
      },
      error: (erro) => {
        console.error('Erro ao cadastrar colaborador', erro);
        alert('Erro ao salvar. Verifique o console.');
      },
    });
  }
}
