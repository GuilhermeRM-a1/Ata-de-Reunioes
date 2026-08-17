import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormArray, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ReuniaoStoreService, ReuniaoInput } from '../../../../core/services/reuniao-store.service';

@Component({
  selector: 'app-reuniao-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './reuniao-form.component.html',
  styleUrl: './reuniao-form.component.scss',
})
export class ReuniaoFormComponent implements OnInit {
  form: FormGroup;
  idEditando: number | null = null;
  idNaoEncontrado = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private store: ReuniaoStoreService,
    private router: Router
  ) {
    this.form = this.fb.group({
      tituloReuniao: ['', [Validators.required, Validators.minLength(5)]],
      dataProcessamento: ['', Validators.required],
      resumoExecutivo: [''],
      status: ['', Validators.required],
      areas: [''],
      participantes: [''],
      pontosChave: [''],
      transcricaoPura: [''],
      acoes: this.fb.array([])
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {
      this.idEditando = Number(idParam);
      const reuniao = this.store.buscarPorId(this.idEditando);

      if (reuniao) {
        this.form.patchValue({
          ...reuniao,
          areas: reuniao.areas.join(', '),
          participantes: reuniao.participantes.join(', ')
        });

        reuniao.acoes.forEach(acao => {
          this.acoes.push(this.fb.group({
            descricao: [acao.descricao, Validators.required],
            tipo: [acao.tipo, Validators.required],
            prazo: [acao.prazo],
            responsavel: [acao.responsavel]
          }));
        });
      } else {
        this.idNaoEncontrado = true;
      }
    }
  }

  get acoes(): FormArray {
    return this.form.get('acoes') as FormArray;
  }

  adicionarAcao(): void {
    const novaAcao = this.fb.group({
      descricao: ['', Validators.required],
      tipo: ['ACAO', Validators.required],
      prazo: [null],
      responsavel: ['']
    });
    this.acoes.push(novaAcao);
  }

  removerAcao(index: number): void {
    this.acoes.removeAt(index);
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const bruto = this.form.value;

    const dados: ReuniaoInput = {
      ...bruto,
      areas: this.textoParaLista(bruto.areas),
      participantes: this.textoParaLista(bruto.participantes),
      acoes: bruto.acoes.map((a: any) => ({
        ...a,
        prazo: a.prazo || null,
        responsavel: a.responsavel || null
      }))
    };

    if (this.idEditando !== null) {
      this.store.atualizar(this.idEditando, dados);
    } else {
      this.store.criar(dados);
    }

    this.router.navigate(['/reunioes']);
  }

  cancelar(): void {
    this.router.navigate(['/reunioes']);
  }

  private textoParaLista(texto: string): string[] {
    return texto
      .split(',')
      .map(item => item.trim())
      .filter(item => item.length > 0);
  }
}