import { Injectable, signal } from '@angular/core';
import { Reuniao } from '../models/reuniao.model';
import { REUNIOES_MOCK } from '../mock/reunioes.mock';

@Injectable({ providedIn: 'root' })
export class ReuniaoStoreService {
  private readonly _reunioes = signal<Reuniao[]>(REUNIOES_MOCK);

  readonly reunioes = this._reunioes.asReadonly();

  listar() {
    return this._reunioes;
  }

  buscarPorId(id: number): Reuniao | undefined {
    return this._reunioes().find(r => r.id === id);
  }

  remover(id: number): void {
    this._reunioes.update(lista => lista.filter(r => r.id !== id));
  }

  adicionar(reuniao: Reuniao): void {
    this._reunioes.update(lista => [...lista, reuniao]);
  }

  atualizar(reuniao: Reuniao): void {
    this._reunioes.update(lista =>
      lista.map(r => (r.id === reuniao.id ? reuniao : r))
    );
  }
}