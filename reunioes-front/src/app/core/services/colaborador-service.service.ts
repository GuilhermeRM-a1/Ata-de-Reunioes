import { HttpClient } from '@angular/common/http';
import { Injectable, ResourceStatus, inject} from '@angular/core';
import { Colaborador } from '../models';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})

export class ColaboradorServiceService {

  http = inject(HttpClient);

  API = "http://localhost:8080/api/reunioes/colaboradores"


  constructor() { }

  save(colaborador: Colaborador): Observable<string>{
    return this.http.post<string>(this.API, colaborador);
  }

  listar(): Observable<Colaborador[]>{
    return this.http.get<Colaborador[]>(this.API);
  }

  buscarPorId(id: number): Observable<Colaborador>{
    return this.http.get<Colaborador>(`${this.API}/${id}`);
  }

  atualizar(id: number, colaborador: Colaborador): Observable<Colaborador>{
    return this.http.put<Colaborador>(`${this.API}/${id}`, colaborador);
  }


  atualizarParcial(id: number, colaborador: Partial<Colaborador>): Observable<Colaborador>{
    return this.http.patch<Colaborador>(`${this.API}/${id}`, colaborador);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }

}
