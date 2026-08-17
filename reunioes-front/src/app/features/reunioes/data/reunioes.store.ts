import { Injectable, signal } from '@angular/core';
import { Reuniao } from '../../../core/models/reuniao.model';

@Injectable({ providedIn: 'root' })
export class ReunioesStore {

  private reunioes = signal<Reuniao[]>([]);

  listar() {
    return this.reunioes.asReadonly();
  }

  obterPorId(id: string): Reuniao | undefined {
    return this.reunioes().find(r => r.id === id);
  }

  criar(dados: Omit<Reuniao, 'id'>): void {
    const novaReuniao: Reuniao = { ...dados, id: crypto.randomUUID() };
    this.reunioes.update(lista => [...lista, novaReuniao]);
  }

  atualizar(id: string, dados: Omit<Reuniao, 'id'>): void {
    this.reunioes.update(lista =>
      lista.map(r => (r.id === id ? { ...dados, id } : r))
    );
  }
}