import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, forkJoin, of } from 'rxjs';
import { MdbFormsModule } from 'mdb-angular-ui-kit/forms';
import { MdbRippleModule } from 'mdb-angular-ui-kit/ripple';
import { catchError, map, switchMap } from 'rxjs/operators';
import { ReuniaoService } from '../../../../core/services/reuniao.service';
import { AcaoService } from '../../../../core/services/acao.service';
import { ColaboradorService } from '../../../../core/services/colaborador.service';
import { AlertaService } from '../../../../core/services/alerta.service';
import { AuthService } from '../../../../core/services/auth.service';
import { AcaoRequestPayload, Colaborador, ReuniaoApiDTO, ReuniaoApiRequest } from '../../../../core/models';

@Component({
  selector: 'app-reuniao-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MdbFormsModule, MdbRippleModule],
  templateUrl: './reuniao-form.component.html',
  styleUrl: './reuniao-form.component.scss'
})
export class ReuniaoFormComponent implements OnInit {
  readonly auth = inject(AuthService);

  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly reuniaoService = inject(ReuniaoService);
  private readonly acaoService = inject(AcaoService);
  private readonly colaboradorService = inject(ColaboradorService);
  private readonly alerta = inject(AlertaService);

  readonly idEditando = signal<number | null>(null);
  readonly idNaoEncontrado = signal(false);
  readonly salvando = signal(false);

  readonly colaboradores = signal<Colaborador[]>([]);

  private readonly acoesOriginais = new Map<number, Omit<AcaoRequestPayload, 'responsavel'>>();

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
    if (!this.auth.isAdmin) {
      this.router.navigate(['/reunioes']);
      return;
    }

    this.colaboradorService.listar().subscribe({
      next: lista => {
        this.colaboradores.set(lista ?? []);
        this.carregarReuniao();
      },
      error: () => {
        this.colaboradores.set([]);
        this.carregarReuniao();
      }
    });
  }

  private carregarReuniao(): void {
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
          const concluida = !!acao.concluida;

          if (acao.id !== null && acao.id !== undefined) {
            this.acoesOriginais.set(Number(acao.id), {
              titulo: acao.titulo ?? '',
              descricao: acao.descricao ?? '',
              tipo: acao.tipo,
              prazo: acao.prazo ?? null,
              concluida
            });
          }

          this.acoes.push(
            this.fb.group({
              id: [acao.id ?? null],
              titulo: [acao.titulo ?? '', Validators.required],
              descricao: [acao.descricao, Validators.required],
              tipo: [acao.tipo, Validators.required],
              prazo: [acao.prazo],
              concluida: [concluida],
              responsavel: [this.idDoPrimeiroResponsavel(acao.responsavel)]
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
        id: [null],
        titulo: ['', Validators.required],
        descricao: ['', Validators.required],
        tipo: ['ACAO', Validators.required],
        prazo: [null],
        concluida: [false],
        responsavel: [null]
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
    const grupos: any[] = bruto.acoes ?? [];

    const acoesExistentes = grupos
      .filter(a => a.id !== null && a.id !== undefined && a.id !== '')
      .map(a => Number(a.id))
      .filter(id => !isNaN(id));

    const acoesNovas = grupos.filter(a => a.id === null || a.id === undefined || a.id === '');

    const acoesComStatusAlterado = grupos.flatMap(grupo => {
      if (grupo.id === null || grupo.id === undefined || grupo.id === '') return [];

      const original = this.acoesOriginais.get(Number(grupo.id));
      const concluida = !!grupo.concluida;

      if (!original || original.concluida === concluida) return [];

      return [{ id: String(grupo.id), titulo: original.titulo, payload: { ...original, concluida } }];
    });

    const dados: ReuniaoApiRequest = {
      titulo: bruto.tituloReuniao,
      data: bruto.dataProcessamento,
      statusTranscricao: bruto.statusTranscricao,
      statusReuniao: bruto.statusReuniao || 'PENDENTE',
      areas: this.textoParaLista(bruto.areas),
      pontosChaves: this.textoParaLinhas(bruto.pontosChave),
      participantes: this.idsDosParticipantes(bruto.participantes),
      acoes: acoesExistentes
    };

    const id = this.idEditando();
    const operacao$: Observable<ReuniaoApiDTO> = id !== null
      ? this.reuniaoService.atualizar(id, dados)
      : this.reuniaoService.criar(dados);

    this.salvando.set(true);

    operacao$
      .pipe(
        switchMap(reuniao => {
          const reuniaoId = reuniao?.id ?? id;

          const criacoes: Observable<string | null>[] = reuniaoId
            ? acoesNovas.map(acao =>
                this.acaoService.criar(reuniaoId, this.paraPayloadDeAcao(acao)).pipe(
                  map(() => null),
                  catchError(() => of(acao.titulo || acao.descricao || 'sem título'))
                )
              )
            : [];

          const atualizacoes: Observable<string | null>[] = acoesComStatusAlterado.map(item =>
            this.acaoService.atualizarParcial(item.id, item.payload).pipe(
              map(() => null),
              catchError(() => of(item.titulo || 'sem título'))
            )
          );

          const chamadas = [...criacoes, ...atualizacoes];

          if (chamadas.length === 0) return of<string[]>([]);

          return forkJoin(chamadas).pipe(
            map(resultados => resultados.filter((t): t is string => !!t))
          );
        })
      )
      .subscribe({
        next: falhas => {
          this.salvando.set(false);

          if (falhas.length > 0) {
            this.alerta.erro(
              id !== null ? 'Reunião atualizada com pendências' : 'Reunião criada com pendências',
              `A reunião foi salva, mas não foi possível salvar ${falhas.length === 1 ? 'a ação' : 'as ações'}: ${falhas.join(', ')}.`
            );
          } else {
            this.alerta.sucesso(id !== null ? 'Reunião atualizada' : 'Reunião criada');
          }

          this.router.navigate(['/reunioes']);
        },
        error: (err: any) => {
          this.salvando.set(false);
          console.error('Erro ao salvar reunião:', err);
        }
      });
  }

  cancelar(): void {
    this.router.navigate(['/reunioes']);
  }

  private paraPayloadDeAcao(acao: any): AcaoRequestPayload {
    const responsavel = Number(acao.responsavel);

    return {
      titulo: acao.titulo,
      descricao: acao.descricao,
      tipo: acao.tipo,
      prazo: acao.prazo || null,
      concluida: !!acao.concluida,
      responsavel: acao.responsavel && !isNaN(responsavel) ? [responsavel] : []
    };
  }

  private idDoPrimeiroResponsavel(responsavel: unknown): number | null {
    if (!Array.isArray(responsavel) || responsavel.length === 0) return null;

    const nome = String(responsavel[0]).trim().toLowerCase();
    const encontrado = this.colaboradores().find(c => c.nome?.trim().toLowerCase() === nome);

    return encontrado?.id ?? null;
  }

  private idsDosParticipantes(texto: string): number[] {
    return this.textoParaLista(texto)
      .map(nome => {
        const alvo = nome.toLowerCase();
        return this.colaboradores().find(c => c.nome?.trim().toLowerCase() === alvo)?.id;
      })
      .filter((id): id is number => typeof id === 'number');
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