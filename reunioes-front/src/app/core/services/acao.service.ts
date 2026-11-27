import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Acao, AcaoRequestPayload } from '../models/acao.model';
import { Pagina } from '../models/pagina.model';

@Injectable({
  providedIn: 'root'
})
export class AcaoService {
  private apiUrl = 'http://localhost:8080/api/reunioes/acoes';

  constructor(private http: HttpClient) {}


listarPagina(concluida: boolean, pagina: number, tamanho: number): Observable<Pagina<Acao>> {
  return this.http.get<Pagina<Acao>>(this.apiUrl, {
    params: { concluida, page: pagina - 1, size: tamanho, sort: ['prazo,asc', 'id,asc'] }
  });
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
