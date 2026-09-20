import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ReuniaoService } from '../../../../core/services/reuniao.service';
import { AlertaService } from '../../../../core/services/alerta.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-reuniao-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reuniao-form.component.html',
  styleUrl: './reuniao-form.component.scss'
})
export class ReuniaoFormComponent implements OnInit {
  // publico de proposito: o template le auth.isAdmin
  readonly auth = inject(AuthService);

  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly reuniaoService = inject(ReuniaoService);
  private readonly alerta = inject(AlertaService);

  readonly idEditando = signal<number | null>(null);
  readonly idNaoEncontrado = signal(false);

  readonly form: FormGroup = this.fb.group({
    tituloReuniao: ['', [Validators.required, Validators.minLength(5)]],
    dataProcessamento: ['', Validators.required],
    resumoExecutivo: [''],
    statusTranscricao: ['', Validators.required],
    statusReuniao: ['', Validators.required],
    areas: [''],
    participantes: [''],
    pontosChave: [''],
    transcricaoPura: [''],
    acoes: this.fb.array([])
  });

  ngOnInit(): void {
    // a tela de cadastro e de edicao e so do admin; usuario comum volta para a listagem
    if (!this.auth.isAdmin) {
      this.router.navigate(['/reunioes']);
      return;
    }

    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam) return;

    const id = Number(idParam);
    if (isNaN(id)) {
      this.idNaoEncontrado.set(true);
      return;
    }

    this.idEditando.set(id);

    this.reuniaoService.buscarPorId(id).subscribe({
      next: (reuniao: any) => {
        if (!reuniao) {
          this.idNaoEncontrado.set(true);
          return;
        }

        this.form.patchValue({
          tituloReuniao: reuniao.titulo,
          dataProcessamento: reuniao.data ? String(reuniao.data).split('T')[0] : '',
          resumoExecutivo: reuniao.resumo ?? reuniao.resumoExecutivo ?? '',
          statusTranscricao: reuniao.statusTranscricao,
          statusReuniao: reuniao.statusReuniao,
          areas: Array.isArray(reuniao.areas) ? reuniao.areas.join(', ') : reuniao.areas,
          participantes: Array.isArray(reuniao.participantes)
            ? reuniao.participantes.join(', ')
            : reuniao.participantes,
          pontosChave: Array.isArray(reuniao.pontosChaves)
            ? reuniao.pontosChaves.join('\n')
            : reuniao.pontosChaves
        });

        for (const acao of reuniao.acoes ?? []) {
          this.acoes.push(
            this.fb.group({
              descricao: [acao.descricao, Validators.required],
              tipo: [acao.tipo, Validators.required],
              prazo: [acao.prazo],
              responsavel: [acao.responsavel]
            })
          );
        }
      },
      error: () => this.idNaoEncontrado.set(true)
    });
  }

  get acoes(): FormArray {
    return this.form.get('acoes') as FormArray;
  }

  adicionarAcao(): void {
    this.acoes.push(
      this.fb.group({
        descricao: ['', Validators.required],
        tipo: ['ACAO', Validators.required],
        prazo: [null],
        responsavel: ['']
      })
    );
  }

  removerAcao(index: number): void {
    this.acoes.removeAt(index);
  }

  salvar(): void {
    if (!this.auth.isAdmin) return;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const bruto = this.form.value;

    const dados: any = {
      ...bruto,
      statusReuniao: bruto.statusReuniao || 'PENDENTE',
      areas: this.textoParaLista(bruto.areas),
      participantes: this.textoParaLista(bruto.participantes),
      pontosChaves: this.textoParaLinhas(bruto.pontosChave),
      acoes: (bruto.acoes ?? []).map((a: any) => ({
        ...a,
        prazo: a.prazo || null,
        responsavel: a.responsavel || null
      }))
    };

    const id = this.idEditando();
    const operacao$ = id !== null
      ? this.reuniaoService.atualizar(id, dados)
      : this.reuniaoService.criar(dados);

    operacao$.subscribe({
      next: () => {
        this.alerta.sucesso(id !== null ? 'Reunião atualizada' : 'Reunião criada');
        this.router.navigate(['/reunioes']);
      },
      error: (err: any) => console.error('Erro ao salvar reunião:', err)
    });
  }

  cancelar(): void {
    this.router.navigate(['/reunioes']);
  }

  private textoParaLista(texto: string): string[] {
    if (!texto) return [];
    return String(texto)
      .split(',')
      .map(item => item.trim())
      .filter(item => item.length > 0);
  }

  private textoParaLinhas(texto: string): string[] {
    if (!texto) return [];
    return String(texto)
      .split('\n')
      .map(item => item.trim())
      .filter(item => item.length > 0);
  }
}
