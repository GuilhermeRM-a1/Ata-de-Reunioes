import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Acao, AcaoRequestPayload } from '../models/acao.model';

@Injectable({
  providedIn: 'root'
})
export class AcaoService {
  private apiUrl = 'http://localhost:8080/api/reunioes/acoes';

  constructor(private http: HttpClient) {}

  /**
   * O endpoint e paginado e responde { content: [...] }, mas ja respondeu
   * array puro. Aceita as duas formas e entrega sempre uma lista.
   */
  listarAcoes(): Observable<Acao[]> {
    return this.http
      .get<Acao[] | { content: Acao[] }>(this.apiUrl)
      .pipe(map(resposta => Array.isArray(resposta) ? resposta : (resposta?.content ?? [])));
  }

  /**
   * A acao so existe dentro de uma reuniao ja salva — por isso o id da
   * reuniao vai na URL e nao no corpo.
   */
  criar(reuniaoId: number, dados: AcaoRequestPayload): Observable<Acao> {
    return this.http.post<Acao>(`${this.apiUrl}/${reuniaoId}`, dados);
  }

  /** O id vem do back como String (AcaoDTO usa String.valueOf). */
  atualizarParcial(id: string, dados: any): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}`, dados);
  }

  remover(reuniaoId: number, id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${reuniaoId}/${id}`);
  }
}
