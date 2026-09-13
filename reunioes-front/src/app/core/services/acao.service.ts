import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Acao } from '../models/acao.model';

@Injectable({
  providedIn: 'root'
})
export class AcaoService {
  private apiUrl = 'http://localhost:8080/api/reunioes/acoes'; 

  constructor(private http: HttpClient) {}

  listarAcoes(): Observable<Acao[]> {
    return this.http.get<Acao[]>(this.apiUrl);
  }

  atualizarParcial(id: number, dados: any): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}`, dados);
  }
}