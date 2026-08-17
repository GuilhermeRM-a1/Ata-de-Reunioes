import { Injectable, computed, signal } from '@angular/core';
import { Reuniao, ReuniaoDetalhe } from '../models';
import { REUNIOES_MOCK } from '../mock/reunioes.mock';

export type ReuniaoInput = Omit<ReuniaoDetalhe, 'id' | 'totalAcoes'>;

@Injectable({ providedIn: 'root' })
export class ReuniaoStoreService {
  private readonly reunioes = signal<ReuniaoDetalhe[]>(
    REUNIOES_MOCK.map((r) => this.clonar(r)),
  );

  readonly listar = computed<Reuniao[]>(() => this.reunioes());
  readonly total = computed(() => this.reunioes().length);

  buscarPorId(id: number): ReuniaoDetalhe | undefined {
    return this.reunioes().find((r) => r.id === id);
  }

  criar(dados: ReuniaoInput): ReuniaoDetalhe {
    const nova: ReuniaoDetalhe = {
      ...this.clonar(dados as ReuniaoDetalhe),
      id: this.proximoId(),
      totalAcoes: dados.acoes.length,
    };
    this.reunioes.update((lista) => [...lista, nova]);
    return nova;
  }

  atualizar(id: number, dados: ReuniaoInput): boolean {
    let alterou = false;
    this.reunioes.update((lista) =>
      lista.map((r) => {
        if (r.id !== id) return r;
        alterou = true;
        return {
          ...this.clonar(dados as ReuniaoDetalhe),
          id,
          totalAcoes: dados.acoes.length,
        };
      }),
    );
    return alterou;
  }

  remover(id: number): boolean {
    const antes = this.reunioes().length;
    this.reunioes.update((lista) => lista.filter((r) => r.id !== id));
    return this.reunioes().length < antes;
  }

  private proximoId(): number {
    const lista = this.reunioes();
    return lista.length === 0 ? 1 : Math.max(...lista.map((r) => r.id)) + 1;
  }

  private clonar(r: ReuniaoDetalhe): ReuniaoDetalhe {
    return {
      ...r,
      participantes: [...r.participantes],
      areas: [...r.areas],
      acoes: r.acoes.map((a) => ({ ...a })),
    };
  }
}