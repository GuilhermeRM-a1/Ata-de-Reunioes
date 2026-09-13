import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReuniaoApiDTO, ReuniaoApiRequest } from '../models/api/reuniao-api.model';

@Injectable({ providedIn: 'root' })
export class ReuniaoService {
  private readonly baseUrl = 'http://localhost:8080/api/reunioes';

  constructor(private http: HttpClient) {}

  listar(): Observable<ReuniaoApiDTO[]> {
    return this.http.get<ReuniaoApiDTO[]>(this.baseUrl);
  }

  buscarPorId(id: number): Observable<ReuniaoApiDTO> {
    return this.http.get<ReuniaoApiDTO>(`${this.baseUrl}/${id}`);
  }

  salvar(dados: ReuniaoApiRequest): Observable<ReuniaoApiDTO> {
    return this.http.post<ReuniaoApiDTO>(this.baseUrl, dados);
  }

  atualizar(id: number, dados: ReuniaoApiRequest): Observable<ReuniaoApiDTO> {
    return this.http.put<ReuniaoApiDTO>(`${this.baseUrl}/${id}`, dados);
  }

  atualizarParcial(id: number, dados: Partial<ReuniaoApiRequest>): Observable<ReuniaoApiDTO> {
    return this.http.patch<ReuniaoApiDTO>(`${this.baseUrl}/${id}`, dados);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}