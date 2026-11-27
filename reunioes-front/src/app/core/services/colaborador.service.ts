import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Colaborador } from '../models';
import { Observable, map } from 'rxjs';
import { API } from '../config/api.config';


@Injectable({
  providedIn: 'root'
})

export class ColaboradorService {

  http = inject(HttpClient);

  private readonly apiUrl = API.colaboradores;


  constructor() { }

  save(colaborador: Colaborador): Observable<string>{
    return this.http.post<string>(this.apiUrl, colaborador);
  }

  /**
   * O endpoint e paginado e responde { content: [...] }, mas ja respondeu
   * array puro. Aceita as duas formas e entrega sempre uma lista.
   */
  listar(): Observable<Colaborador[]> {
    return this.http
      .get<Colaborador[] | { content: Colaborador[] }>(this.apiUrl)
      .pipe(map(resposta => Array.isArray(resposta) ? resposta : (resposta?.content ?? [])));
  }

  buscarPorId(id: number): Observable<Colaborador>{
    return this.http.get<Colaborador>(`${this.apiUrl}/${id}`);
  }

  buscarPorEmail(email: string): Observable<Colaborador> {
    return this.http.get<Colaborador>(`${this.apiUrl}/email/${email}`);
  }

  atualizar(id: number, colaborador: Colaborador): Observable<Colaborador>{
    return this.http.put<Colaborador>(`${this.apiUrl}/${id}`, colaborador);
  }


  atualizarParcial(id: number, colaborador: Partial<Colaborador>): Observable<Colaborador>{
    return this.http.patch<Colaborador>(`${this.apiUrl}/${id}`, colaborador);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

}
