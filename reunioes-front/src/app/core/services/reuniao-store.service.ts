import { Injectable, computed, signal } from '@angular/core';
import { Reuniao, ReuniaoDetalhe } from '../models';
import { REUNIOES_MOCK } from '../mock/reunioes.mock';

/** Campos que o formulario controla — id e totalAcoes sao derivados. */
export type ReuniaoInput = Omit<ReuniaoDetalhe, 'id' | 'totalAcoes'>;

/**
 * Estado em memoria das reunioes. Nenhum HttpClient aqui: quando a
 * integracao entrar, so esta classe muda e nenhuma tela e tocada.
 */
@Injectable({ providedIn: 'root' })
export class ReuniaoStoreService {
  private readonly _reunioes = signal<ReuniaoDetalhe[]>(
    REUNIOES_MOCK.map((r: ReuniaoDetalhe) => this.clonar(r)),
  );

  /** Lista reativa — exposta como `reunioes` para ser chamada como reunioes() na view */
  readonly reunioes = computed<Reuniao[]>(() => this._reunioes());

  readonly total = computed<number>(() => this._reunioes().length);

  buscarPorId(id: number): ReuniaoDetalhe | undefined {
    const item = this._reunioes().find((r: ReuniaoDetalhe) => r.id === id);
    return item ? this.clonar(item) : undefined;
  }

  criar(dados: ReuniaoInput): ReuniaoDetalhe {
    const nova: ReuniaoDetalhe = {
      ...this.clonar(dados as ReuniaoDetalhe),
      id: this.proximoId(),
      totalAcoes: dados.acoes?.length || 0,
    };
    this._reunioes.update((lista: ReuniaoDetalhe[]) => [...lista, nova]);
    return nova;
  }

  /** Retorna false quando o id nao existe, em vez de estourar. */
  atualizar(id: number, dados: ReuniaoInput): boolean {
    let alterou = false;
    this._reunioes.update((lista: ReuniaoDetalhe[]) =>
      lista.map((r: ReuniaoDetalhe) => {
        if (r.id !== id) {
          return r;
        }
        alterou = true;
        return {
          ...this.clonar(dados as ReuniaoDetalhe),
          id,
          totalAcoes: dados.acoes?.length || 0,
        };
      }),
    );
    return alterou;
  }

  remover(id: number): boolean {
    const antes = this._reunioes().length;
    this._reunioes.update((lista: ReuniaoDetalhe[]) =>
      lista.filter((r: ReuniaoDetalhe) => r.id !== id)
    );
    return this._reunioes().length < antes;
  }

  private proximoId(): number {
    const lista = this._reunioes();
    return lista.length === 0 ? 1 : Math.max(...lista.map((r: ReuniaoDetalhe) => r.id)) + 1;
  }

  /** Copia profunda o bastante para o form nunca mutar o estado do store. */
  private clonar(r: ReuniaoDetalhe): ReuniaoDetalhe {
    return {
      ...r,
      participantes: r.participantes ? [...r.participantes] : [],
      areas: r.areas ? [...r.areas] : [],
      acoes: r.acoes ? r.acoes.map((a) => ({ ...a })) : [],
    };
  }
}