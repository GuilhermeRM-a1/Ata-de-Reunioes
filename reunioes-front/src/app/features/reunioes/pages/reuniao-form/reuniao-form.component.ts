import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, forkJoin, of } from 'rxjs';
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
  private readonly acaoService = inject(AcaoService);
  private readonly colaboradorService = inject(ColaboradorService);
  private readonly alerta = inject(AlertaService);

  readonly idEditando = signal<number | null>(null);
  readonly idNaoEncontrado = signal(false);
  readonly salvando = signal(false);

  /** Alimenta o select de responsavel de cada acao. */
  readonly colaboradores = signal<Colaborador[]>([]);

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

    // os colaboradores vem antes da reuniao porque o responsavel de uma acao
    // ja salva volta da API como nome e precisa ser reconvertido em id
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
          // o id marca a acao que ja existe no banco: ela nao pode ser recriada
          this.acoes.push(
            this.fb.group({
              id: [acao.id ?? null],
              titulo: [acao.titulo ?? '', Validators.required],
              descricao: [acao.descricao, Validators.required],
              tipo: [acao.tipo, Validators.required],
              prazo: [acao.prazo],
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

    // a acao so pode ser criada debaixo de uma reuniao que ja existe, entao o
    // corpo da reuniao leva apenas os ids das acoes que ja estao no banco
    const acoesExistentes = grupos
      .filter(a => a.id !== null && a.id !== undefined && a.id !== '')
      .map(a => Number(a.id))
      .filter(id => !isNaN(id));

    const acoesNovas = grupos.filter(a => a.id === null || a.id === undefined || a.id === '');

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

          // forkJoin([]) completa sem emitir: sem acao nova, nem entra no passo 2
          if (!reuniaoId || acoesNovas.length === 0) return of<string[]>([]);

          return forkJoin(
            acoesNovas.map(acao =>
              this.acaoService.criar(reuniaoId, this.paraPayloadDeAcao(acao)).pipe(
                map(() => null),
                // uma acao que falha nao derruba as outras nem apaga a reuniao ja salva
                catchError(() => of(acao.titulo || acao.descricao || 'sem título'))
              )
            )
          ).pipe(map(resultados => resultados.filter((t): t is string => !!t)));
        })
      )
      .subscribe({
        next: falhas => {
          this.salvando.set(false);

          if (falhas.length > 0) {
            this.alerta.erro(
              id !== null ? 'Reunião atualizada com pendências' : 'Reunião criada com pendências',
              `A reunião foi salva, mas não foi possível criar ${falhas.length === 1 ? 'a ação' : 'as ações'}: ${falhas.join(', ')}.`
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
      concluida: false,
      responsavel: acao.responsavel && !isNaN(responsavel) ? [responsavel] : []
    };
  }

  /** O back devolve o responsavel como nome; o select trabalha com id. */
  private idDoPrimeiroResponsavel(responsavel: unknown): number | null {
    if (!Array.isArray(responsavel) || responsavel.length === 0) return null;

    const nome = String(responsavel[0]).trim().toLowerCase();
    const encontrado = this.colaboradores().find(c => c.nome?.trim().toLowerCase() === nome);

    return encontrado?.id ?? null;
  }

  /** O campo e texto livre, mas o back espera ids — nome desconhecido e descartado. */
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
