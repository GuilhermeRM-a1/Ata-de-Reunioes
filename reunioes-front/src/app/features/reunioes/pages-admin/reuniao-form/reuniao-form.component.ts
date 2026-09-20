import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormArray, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ReuniaoStoreService } from '../../../../core/services/reuniao.service';

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
      statusTranscricao: ['', Validators.required],
      statusReuniao: ['', Validators.required],
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

      // buscarPorId retorna Observable, por isso  subscribe
      this.store.buscarPorId(this.idEditando).subscribe({
        next: (reuniao: any) => {
          if (reuniao) {
            this.form.patchValue({
              tituloReuniao: reuniao.titulo,
              dataProcessamento: reuniao.data ? reuniao.data.split('T')[0] : '',
              resumoExecutivo: reuniao.resumoExecutivo,
              statusTranscricao: reuniao.statusTranscricao,
              statusReuniao: reuniao.statusReuniao,
              areas: Array.isArray(reuniao.areas) ? reuniao.areas.join(', ') : reuniao.areas,
              participantes: Array.isArray(reuniao.participantes) ? reuniao.participantes.join(', ') : reuniao.participantes,
              pontosChave: Array.isArray(reuniao.pontosChaves) ? reuniao.pontosChaves.join('\n') : reuniao.pontosChaves
            });

            if (reuniao.acoes) {
              reuniao.acoes.forEach((acao: any) => {
                this.acoes.push(this.fb.group({
                  descricao: [acao.descricao, Validators.required],
                  tipo: [acao.tipo, Validators.required],
                  prazo: [acao.prazo],
                  responsavel: [acao.responsavel]
                }));
              });
            }
          } else {
            this.idNaoEncontrado = true;
          }
        },
        error: () => {
          this.idNaoEncontrado = true;
        }
      });
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

    const dados = {
      ...bruto,
      statusTranscricao: bruto.statusTranscricao,
      statusReuniao: bruto.statusReuniao || 'PENDENTE',
      areas: this.textoParaLista(bruto.areas),
      participantes: this.textoParaLista(bruto.participantes),
      acoes: bruto.acoes.map((a: any) => ({
        ...a,
        prazo: a.prazo || null,
        responsavel: a.responsavel || null
      }))
    };

    // Define se vai atualizar ou criar e se inscreve para aguardar o backend responder
    const operacao$ = this.idEditando !== null
      ? this.store.atualizar(this.idEditando, dados)
      : this.store.criar(dados);

    operacao$.subscribe({
      next: () => {
        this.router.navigate(['/admin/reunioes']);
      },
      error: (err) => {
        console.error('Erro ao salvar reunião:', err);
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/admin/reunioes']);
  }

  private textoParaLista(texto: string): string[] {
    if (!texto) return [];
    return String(texto)
      .split(',')
      .map(item => item.trim())
      .filter(item => item.length > 0);
  }
}