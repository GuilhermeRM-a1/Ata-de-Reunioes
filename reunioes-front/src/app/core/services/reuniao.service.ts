import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReuniaoApiDTO, ReuniaoApiRequest } from '../models/api/reuniao-api.model';
import { API } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class ReuniaoService {
  private readonly apiUrl = API.reunioes;

  constructor(private http: HttpClient) {

  }

  listar(): Observable<ReuniaoApiDTO[]> {
      return this.http.get<ReuniaoApiDTO[]>(this.apiUrl);
    }

  buscarPorId(id: number): Observable<ReuniaoApiDTO> {
    return this.http.get<ReuniaoApiDTO>(`${this.apiUrl}/${id}`);
  }


  criar(dados: ReuniaoApiRequest): Observable<ReuniaoApiDTO> {
    return this.http.post<ReuniaoApiDTO>(this.apiUrl, dados);
  }

  atualizar(id: number, dados: ReuniaoApiRequest): Observable<ReuniaoApiDTO> {
    return this.http.put<ReuniaoApiDTO>(`${this.apiUrl}/${id}`, dados);
  }


  remover(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}